package pt.isel.ncml.objectivedb

/**
 * Created by Mario on 2017-06-14.
 */
interface IInternalLock : AutoCloseable{
    fun lock()
    fun unlock()
}