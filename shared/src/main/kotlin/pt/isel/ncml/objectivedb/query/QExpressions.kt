package pt.isel.ncml.objectivedb.query

/**
 * Created by nuno on 5/8/17.
 */

interface AttrExprVisitor {
    fun visit(expr: EqAttrExpr)
    fun visit(expr: LtAttrExpr)
    fun visit(expr: LteAttrExpr)
    fun visit(expr: GtAttrExpr)
    fun visit(expr: GteAttrExpr)
    fun visit(expr: NeqAttrExpr)
    fun visit(expr: ConstantExpr)
    fun visit(expr: MemoryExpr)
}

interface AttrExpr : QExpr {

    val value : Any
    val fieldName : String
    fun accept(visitor: AttrExprVisitor)

}

interface MemoryExpr : AttrExpr {
    val filter : (Any) -> Boolean
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface ConstantExpr : AttrExpr {
    override val value : Boolean

    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

object TRUE : ConstantExpr {
    override val fieldName: String
        get() = throw UnsupportedOperationException()
    override val value: Boolean
        get() = true

    override fun toString(): String {
        return "ConstantExpr:True"
    }
}

object FALSE : ConstantExpr {
    override val fieldName: String
        get() = throw UnsupportedOperationException()
    override val value: Boolean
        get() = false

    override fun toString(): String {
        return "ConstantExpr:False"
    }
}

interface NeqAttrExpr : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface GteAttrExpr : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface GtAttrExpr : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface LtAttrExpr : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface LteAttrExpr : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface EqAttrExpr : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(this)
    }
}

interface QExpr

interface OperExprVisitor {
    fun visit(expr: AndQExpr)
    fun visit(expr: OrQExpr)
    fun visit(expr: InnerQExpr)
}

interface InnerQExpr : OperQExpr {
    val inner : OperQExpr

    override fun accept(visitor: OperExprVisitor) {
        visitor.visit(this)
    }
}

interface OperQExpr : QExpr {
    val left : QExpr
    val right : QExpr

    fun accept(visitor: OperExprVisitor)
}

interface OrQExpr : OperQExpr {
    override fun accept(visitor: OperExprVisitor) {
        visitor.visit(this)
    }
}

interface AndQExpr : OperQExpr {
    override fun accept(visitor: OperExprVisitor) {
        visitor.visit(this)
    }
}

abstract class QExprBase(override val left: QExpr, override val right: QExpr) : OperQExpr

class AndQExprImp(left: QExpr, right: QExpr) : QExprBase(left, right), AndQExpr
class OrQExprImp(left: QExpr, right: QExpr) : QExprBase(left, right), OrQExpr
class SingleQExprImpl(right: QExpr) : QExprBase(FALSE, right), OrQExpr
class InnerQExprImpl(override val inner : OperQExpr) : InnerQExpr {
    override val left: QExpr
        get() = throw UnsupportedOperationException()
    override val right: QExpr
        get() = throw UnsupportedOperationException()
}

sealed class ExprImpl : AttrExpr{
    data class Equals(override val value: Any, override val fieldName: String) : EqAttrExpr
    data class Lt(override val value: Any, override val fieldName: String) : LtAttrExpr
    data class Lte(override val value: Any, override val fieldName: String) : LteAttrExpr
    data class Gt(override val value: Any, override val fieldName: String) : GtAttrExpr
    data class Gte(override val value: Any, override val fieldName: String) : GteAttrExpr
    data class NEquals(override val value: Any, override val fieldName: String) : NeqAttrExpr
    data class Constant(override val value: Boolean, override val fieldName: String) : ConstantExpr
    data class Memory<T>(private val _filter: (T) -> Boolean) : MemoryExpr {
        override val fieldName: String
            get() = throw UnsupportedOperationException()
        override val value: Any
            get() = throw UnsupportedOperationException()
        @Suppress("UNCHECKED_CAST")
        override val filter: (Any) -> Boolean
            get() = _filter as (Any)->Boolean
    }
}
