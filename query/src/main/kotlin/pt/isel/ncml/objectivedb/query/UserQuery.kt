package pt.isel.ncml.objectivedb.query

/**
 * Created by nuno on 5/8/17.
 */

interface UserQuery {
    /**
     * Returns an OO query interface that evaluates expressions from the opposite order they were written.
     *
     * If one writes **where().and().where().or()** the result will be a [OrQExp] with right the last where and
     * on the left and [AndQExpr] with the two remaining where.
     */
    fun <T> from(clazz:Class<T>): StartClassQuery<T, ReadyQuery<T>>

    /**
     * Returns all the items from the class
     */
    fun <T> allFrom(clazz:Class<T>) : Collection<T>
}

interface ReadyQuery<T> {
    fun query(): Collection<T>
}

interface StartClassQuery<T, P> {
    /**
     * Must be executed in memory. Useful for complex calculations
     */
    fun where(filter : (T) -> Boolean) : Joiner<EndClassQuery<T, P>, P>

    /**
     * Can take advantage of db indexes and should be executed first
     */
    fun where(expression:AttrExpr) : Joiner<EndClassQuery<T, P>, P>

}

interface EndClassQuery<T, P> : StartClassQuery<T, P>{

    fun inner() : StartClassQuery<T, Joiner<EndClassQuery<T, P>, P>>

}

interface Joiner<N, P> {
    /**
     * Can take advantage of group theory
     */
    fun and() : N
    /**
     * Can not take advantage of group theory
     */
    fun or() : N

    fun end() : P

}


