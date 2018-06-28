package pt.isel.ncml.objectivedb

import pt.isel.ncml.objectivedb.index.BestMatchIndexFilter
import pt.isel.ncml.objectivedb.index.CombinerIndexFilter
import pt.isel.ncml.objectivedb.index.ConfigIndexes
import pt.isel.ncml.objectivedb.index.IndexFilter
import pt.isel.ncml.objectivedb.load.Source
import pt.isel.ncml.objectivedb.query.*
import pt.isel.ncml.objectivedb.serialization.ISerializer
import pt.isel.ncml.objectivedb.storage.DbResult
import pt.isel.ncml.objectivedb.storage.IPersistentStorage
import pt.isel.ncml.objectivedb.util.ByteConverter
import pt.isel.ncml.objectivedb.util.reflection.ReflectionUtils
import java.util.*
import javax.inject.Inject

/**
 * Created by Nuno on 22/05/2017.
 */

private fun Collection<DbResult>.resolve(serializer: ISerializer): Collection<Any> {
    return this.map { entry -> Source(entry.value.byteStream, entry.key) }
            .map { source -> serializer.deserialize(source) }
            .filterNotNull()
            .toList()
}

class QueryAnalyzer @Inject constructor(
        private val storage:IPersistentStorage,
        private val serializer: ISerializer,
        private val lock:ILock,
        private val configIndexes: ConfigIndexes
) : DbQueryInterface {

    override fun query(clazz: Class<*>): Collection<Any> {
        try {
            lock.beginRead()
            return storage
                    .queryIndex(ByteConverter.fromObject(clazz))
                    .resolve(serializer)
        }finally {
            lock.finishRead()
        }
    }

    override fun query(expr: OperQExpr, clazz: Class<*>): Collection<Any> {
        try{
            lock.beginRead()
            val analyzer = getAnalyzer(expr, clazz, configIndexes)
            return storage
                    .queryIndex(analyzer.immutableIndexes)
                    .resolve(serializer)
                    .filter(analyzer.memFilter)
        } finally {
            lock.finishRead()
        }

    }
}

private fun  getAnalyzer(expr: OperQExpr, clazz: Class<*>, configIndexes: ConfigIndexes): OperExprAnalyzer {
    val visitor = OperExprAnalyzer(clazz, configIndexes)
    expr.accept(visitor)
    return visitor
}

private typealias MemFilter = (Any) -> Boolean

internal class OperExprAnalyzer(
        private val clazz: Class<*>,
        private val configIndexes: ConfigIndexes
) : OperExprVisitor, AttrExprVisitor {

    private var filter : MemFilter = {true}
    private var indexFilter: IndexFilter = BestMatchIndexFilter(clazz, configIndexes)

    val immutableIndexes: ByteArray
        get() = indexFilter.immutableIndex()

    val memFilter: MemFilter
        get() = filter

    override fun visit(expr: AndQExpr) {
        filter = {
            val vLeft = OperExprAnalyzer(clazz, configIndexes)
            val left: QExpr = expr.left
            when(left) {
                is OperQExpr -> left.accept(vLeft)
                is AttrExpr -> left.accept(vLeft)
            }

            val vRight = OperExprAnalyzer(clazz, configIndexes)
            val right: QExpr = expr.right
            when(right) {
                is OperQExpr -> right.accept(vRight)
                is AttrExpr -> right.accept(vRight)
            }

            indexFilter = CombinerIndexFilter(vLeft::indexFilter, vRight::indexFilter)
            val lRes = vLeft.filter(it)
            val rRes = vRight.filter(it)
            lRes && rRes
        }
    }


    override fun visit(expr: OrQExpr) {
        filter = {
            val vLeft = OperExprAnalyzer(clazz, configIndexes)
            val left: QExpr = expr.left
            when(left) {
                is OperQExpr -> left.accept(vLeft)
                is AttrExpr -> left.accept(vLeft)
            }

            val vRight = OperExprAnalyzer(clazz, configIndexes)
            val right: QExpr = expr.right
            when(right) {
                is OperQExpr -> right.accept(vRight)
                is AttrExpr -> right.accept(vRight)
            }

            indexFilter = CombinerIndexFilter(vLeft::indexFilter, vRight::indexFilter)
            val lRes = vLeft.filter(it)
            val rRes = vRight.filter(it)
            lRes || rRes
        }
    }

    override fun visit(expr: InnerQExpr) {
        filter = {
            val inner = OperExprAnalyzer(clazz, configIndexes)
            expr.inner.accept(inner)
            inner.filter(it)
        }
    }

    override fun visit(expr: MemoryExpr) {
        filter = expr.filter
    }

    override fun visit(expr: LtAttrExpr) {
        filter = {
            val field = ReflectionUtils.getField(clazz, expr.fieldName, true)
            field.isAccessible = true

            val data = field.get(it) as? Comparable<*> ?: throw UnsupportedOperationException("Field ${expr.fieldName} is not comparable")
            @Suppress("UNCHECKED_CAST")
            (data as Comparable<Any>) < expr.value
        }
    }

    override fun visit(expr: LteAttrExpr) {
        filter = {
            val field = ReflectionUtils.getField(clazz, expr.fieldName, true)
            field.isAccessible = true

            val data = field.get(it) as? Comparable<*> ?: throw UnsupportedOperationException("Field ${expr.fieldName} is not comparable")
            @Suppress("UNCHECKED_CAST")
            (data as Comparable<Any>) <= expr.value
        }
    }

    override fun visit(expr: GtAttrExpr) {
        filter = {
            val field = ReflectionUtils.getField(clazz, expr.fieldName, true)
            field.isAccessible = true

            val data = field.get(it) as? Comparable<*> ?: throw UnsupportedOperationException("Field ${expr.fieldName} is not comparable")
            @Suppress("UNCHECKED_CAST")
            (data as Comparable<Any>) > expr.value
        }
    }

    override fun visit(expr: GteAttrExpr) {
        filter = {
            val field = ReflectionUtils.getField(clazz, expr.fieldName, true)
            field.isAccessible = true

            val data = field.get(it) as? Comparable<*> ?: throw UnsupportedOperationException("Field ${expr.fieldName} is not comparable")
            @Suppress("UNCHECKED_CAST")
            (data as Comparable<Any>) >= expr.value
        }
    }

    override fun visit(expr: NeqAttrExpr) {
        filter = {
            val field = ReflectionUtils.getField(clazz, expr.fieldName, true)
            field.isAccessible = true
            val data = field.get(it)
            data !== expr.value && !Objects.equals(expr.value, data)
        }
    }

    override fun visit(expr: EqAttrExpr) {
        indexFilter.filterField(expr.fieldName, expr.value)
        filter = {
            val field = ReflectionUtils.getField(clazz, expr.fieldName, true)
            field.isAccessible = true
            val data = field.get(it)
            data === expr.value || Objects.equals(expr.value, data)
        }
    }

    override fun visit(expr: ConstantExpr) {
        filter = { expr.value }
    }
}

