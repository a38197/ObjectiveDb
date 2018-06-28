package pt.isel.ncml.objectivedb.reflector;

import dagger.Module;
import dagger.Provides;
import pt.isel.ncml.objectivedb.reflector.inspector.IClassInspector;

import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by Mario on 2017-04-18.
 */
@Module
public class ReflectorModule {

    @Provides
    @Singleton
    public IReflector providesReflector(ObjectReflector objectReflector){
        return objectReflector;
    }

    @Provides
    @Singleton
    public IMetaStore providesMetaStore(MetaStoragePropertiesLoader metaLoader) {
        return metaLoader;
    }

    @Provides
    @Singleton
    //can be in a new module and provider will not be needed anymore
    public IClassInspector.IClassInspectorFunction providesInspectorGetter(Provider<IMetaStore> metaStore) {
        return cls -> metaStore.get().getClassInspector(cls);
    }
}
