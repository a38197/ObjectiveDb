package pt.isel.ncml.objectivedb

import dagger.Module
import dagger.Provides
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Singleton

/**
 * Created by Mario on 2017-04-12.
 */
@Module
class ObjectiveLockModule(private val supplier :()->ILock) {

    @Provides
    fun providesILock(): ILock {
        return supplier()
    }

    @Provides
    @Singleton
    fun providesInternalLock(): IInternalLock {
        return InternalLock(ReentrantLock())
    }
}