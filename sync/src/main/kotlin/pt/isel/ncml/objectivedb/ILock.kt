package pt.isel.ncml.objectivedb

/**
 * User definable interface that has broad control over database operations
 */
interface ILock {
    /**
     * Invoked when a write operation occurs on the database
     */
    fun beginWrite()
    /**
     * Invoked when a write operation finishes on the database
     */
    fun finishWrite()
    /**
     * Invoked when a read operation occurs on the database
     */
    fun beginRead()
    /**
     * Invoked when a read operation finishes on the database
     */
    fun finishRead()
}