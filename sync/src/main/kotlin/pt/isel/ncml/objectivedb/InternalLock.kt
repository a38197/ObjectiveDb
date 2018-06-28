package pt.isel.ncml.objectivedb

import java.util.concurrent.locks.ReentrantLock

/**
 * Created by Mario on 2017-06-14.
 */
class InternalLock(private val lock: ReentrantLock) : IInternalLock{
    override fun close() {
        lock.unlock()
    }

    override fun lock() {
        lock.lock()
    }

    override fun unlock() {
        lock.unlock()
    }
}