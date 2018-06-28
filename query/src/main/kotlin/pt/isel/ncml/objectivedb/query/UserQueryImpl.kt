package pt.isel.ncml.objectivedb.query

/**
 * Created by nuno on 5/11/17.
 */
class UserQueryImpl(private val dbInterface: DbQueryInterface) : UserQuery, ReadyQuery<Unit> {
    override fun <T> from(clazz: Class<T>): StartClassQuery<T, ReadyQuery<T>> {
        val context = QueryContext(dbInterface, clazz)
        return context
    }

    override fun query(): Collection<Unit> {
        return emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> allFrom(clazz: Class<T>): Collection<T> {
        return dbInterface.query(clazz) as Collection<T>
    }
}


private class QueryContext<T>(private val dbInterface: DbQueryInterface, private val clazz: Class<T>) : ReadyQuery<T>, StartClassQuery<T, ReadyQuery<T>> {

    private var qBuilder = QueryBuilder()

    override fun where(filter: (T) -> Boolean): Joiner<EndClassQuery<T, ReadyQuery<T>>, ReadyQuery<T>> {
        return StartClassQueryImpl<T, ReadyQuery<T>>(this).where(filter)
    }

    override fun where(expression: AttrExpr): Joiner<EndClassQuery<T, ReadyQuery<T>>, ReadyQuery<T>> {
        return StartClassQueryImpl<T, ReadyQuery<T>>(this).where(expression)
    }

    @Suppress("UNCHECKED_CAST")
    override fun query(): Collection<T> {
        return dbInterface.query(qBuilder.first(), clazz) as Collection<T>
    }

    inner private open class StartClassQueryImpl<T, P>(protected val parent: P) : StartClassQuery<T, P> {

        override fun where(filter: (T) -> Boolean): Joiner<EndClassQuery<T, P>, P> {
            qBuilder.where(ExprImpl.Memory(filter))
            return JoinerImpl({ EndClassQueryImpl<T, P>(parent) }, parent)
        }

        override fun where(expression: AttrExpr): Joiner<EndClassQuery<T, P>, P> {
            qBuilder.where(expression)
            return JoinerImpl({ EndClassQueryImpl<T, P>(parent) }, parent)
        }
    }

    inner private class EndClassQueryImpl<T, P>(p : P) : StartClassQueryImpl<T, P>(p), EndClassQuery<T, P> {

        override fun inner(): StartClassQuery<T, Joiner<EndClassQuery<T, P>, P>> {
            qBuilder = qBuilder.inner()
            return StartClassQueryImpl<T, Joiner<EndClassQuery<T, P>, P>>(
                    JoinerImpl({ EndClassQueryImpl<T, P>(parent) }, parent)
            )
        }

    }

    inner private class JoinerImpl<N, P>(
            private val nFactory:()->N,
            private val parent: P
    ) : Joiner<N, P> {

        override fun and(): N {
            this@QueryContext.qBuilder.and()
            return nFactory()
        }

        override fun or(): N {
            this@QueryContext.qBuilder.or()
            return nFactory()
        }

        override fun end(): P{
            qBuilder = qBuilder.end()
            return parent
        }
    }

}

/**
 * Constructs a chain of expressions in the reverse order as they are declared.
 * Does not take into acount operator precedence
 */
private class QueryBuilder(private val parent: QueryBuilder?) {

    constructor() : this(null)

    private lateinit var temp : QExpr
    private lateinit var curr : OperQExpr
    private var counter = 0

    private var where: (QExpr)->Unit = {
        temp = it
    }

    private var and : () -> Unit = {
        where = {
            curr = AndQExprImp(temp, it)
            and = nextAnd
            or = nextOr
        }
    }

    private val nextAnd : ()->Unit = {
        where = {
            curr = AndQExprImp(curr, it)
        }
    }

    private var or : () -> Unit = {
        where = {
            curr = OrQExprImp(temp, it)
            and = nextAnd
            or = nextOr
        }
    }

    private val nextOr : ()->Unit = {
        where = {
            curr = OrQExprImp(curr, it)
        }
    }


    fun where(expr: QExpr){
        counter++
        where.invoke(expr)
    }

    fun and() = and.invoke()

    fun or() = or.invoke()

    fun inner() : QueryBuilder {
        return QueryBuilder(this)
    }

    fun end() : QueryBuilder {
        return if(parent == null) this
        else {
            parent.where(InnerQExprImpl(this.first()))
            return parent
        }
    }

    fun first() : OperQExpr {
        return if(counter == 1) SingleQExprImpl(temp) else curr
    }
}
