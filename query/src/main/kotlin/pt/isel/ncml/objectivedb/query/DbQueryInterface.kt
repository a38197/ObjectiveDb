package pt.isel.ncml.objectivedb.query

/**
 * Created by nuno on 5/16/17.
 */
interface DbQueryInterface {
    fun query(expr: OperQExpr, clazz: Class<*>): Collection<Any>
    fun query(clazz: Class<*>): Collection<Any>
}