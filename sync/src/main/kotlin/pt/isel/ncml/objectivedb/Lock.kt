package pt.isel.ncml.objectivedb

import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Created by Mario on 2017-04-12.
 */
class Lock constructor(val rwLock: ReentrantReadWriteLock) : ILock {
    override fun beginWrite() {
        rwLock.writeLock().lock()
    }

    override fun finishWrite() {
        rwLock.writeLock().unlock()
    }

    override fun beginRead() {
        rwLock.readLock().lock()
    }

    override fun finishRead() {
        rwLock.readLock().unlock()
    }

}
