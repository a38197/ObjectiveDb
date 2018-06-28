package pt.isel.ncml.objectivedb.serialization

import dagger.Module
import dagger.Provides
import org.objenesis.Objenesis
import org.objenesis.ObjenesisStd
import pt.isel.ncml.objectivedb.serialization.instantiation.DefaultInstanceFactory
import pt.isel.ncml.objectivedb.serialization.instantiation.IInstanceFactory
import javax.inject.Singleton

/**
 * Created by mlourenc on 4/18/2017.
 */
@Module
class SerializationModule(private val filter : IStoreFilter) {

    @Provides
    @Singleton
    fun providesStorageFilter() : IStoreFilter {
        return filter
    }

    @Provides
    @Singleton
    fun providesSerializer(serializer: Serializer):ISerializer{
        return serializer
    }

    @Provides
    @Singleton
    fun providesDefaultInstanceFactory(factory: DefaultInstanceFactory) : IInstanceFactory {
        return factory
    }

    @Provides
    fun providesObjenesis(): Objenesis {
        return ObjenesisStd()
    }
}
