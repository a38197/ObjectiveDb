package pt.isel.ncml.objectivedb.query

/**
 * Created by nuno on 5/16/17.
 */

class MockDbInterface : DbQueryInterface {

    override fun query(clazz: Class<*>): Collection<Any> {
        return emptyList()
    }

    lateinit var expr : OperQExpr

    override fun query(expr: OperQExpr, clazz: Class<*>): Collection<Any> {
        this.expr = expr
        return emptyList()
    }

}