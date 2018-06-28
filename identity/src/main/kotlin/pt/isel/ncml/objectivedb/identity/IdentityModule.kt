package pt.isel.ncml.objectivedb.identity

import com.google.common.collect.MapMaker
import dagger.Module
import dagger.Provides
import pt.isel.ncml.objectivedb.IDbIdentity
import java.util.concurrent.ConcurrentMap
import javax.inject.Singleton

/**
 * Created by mlourenc on 4/18/2017.
 */
@Module
class IdentityModule() {



    @Provides
    @Singleton
    fun providesObjectToDbIdentityWeakMap() : ConcurrentMap<Any, IIdentityService.ObjectInfo>{
        return MapMaker().weakKeys().makeMap<Any, IIdentityService.ObjectInfo>()
    }

    @Provides
    @Singleton
    fun providesIdentityService(identity: IdentityService) : IIdentityService{
        return identity
    }

    @Provides
    @Singleton
    fun providesIdentityCache(cache : IdentityCache): IIdentityCache{
        return cache
    }

    @Provides
    @Singleton
    fun providesKeyToObjectMap() : ConcurrentMap<IRefIdentity, Any> {
        return MapMaker().weakValues().makeMap<IRefIdentity, Any>()
    }


    @Provides
    @Singleton
    fun providesDbKeyToObjectMap() : ConcurrentMap<IDbIdentity, Any> {
        return MapMaker().weakValues().makeMap<IDbIdentity, Any>()
    }
}
