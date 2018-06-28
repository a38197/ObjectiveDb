package pt.isel.ncml.objectivedb.storage

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Mario on 2017-04-19.
 */
@Module
class StorageModule {

    @Provides
    @Singleton
    fun providesPersistentStorage(storage: PersistentStorage): IPersistentStorage{
        return storage
    }
}
