package pt.isel.ncml.objectivedb

import dagger.Module
import dagger.Provides
import pt.isel.ncml.objectivedb.load.LoadFactory
import pt.isel.ncml.objectivedb.query.DbQueryInterface
import pt.isel.ncml.objectivedb.reference.IReferenceHandler
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver
import pt.isel.ncml.objectivedb.serialization.ILoadFactory
import javax.inject.Singleton

/**
 * Created by mlourenc on 4/18/2017.
 */
@Module
class ObjectiveDbModule {

    @Provides
    @Singleton
    fun objectiveDbProvider(db:ObjDbImplement): ObjectiveDB {
        return db
    }

    @Provides
    @Singleton
    fun providesLoadFactory(factory: LoadFactory): ILoadFactory {
        return factory
    }

    @Provides
    @Singleton
    fun providesSerializerResolver(resolver: KeyGenerator): IReferenceHandler{
        return resolver
    }

    @Provides
    @Singleton
    fun providesObjectResolver(resolver: KeyGenerator): IObjectResolver{
        return resolver
    }

    @Provides
    @Singleton
    fun dbQueryInterface(analyzer: QueryAnalyzer):DbQueryInterface {
        return analyzer
    }

}
