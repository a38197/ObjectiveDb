package pt.isel.ncml.objectivedb.reflector;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import pt.isel.ncml.objectivedb.reflector.inspector.ClassInspector;
import pt.isel.ncml.objectivedb.reflector.inspector.IClassInspector;
import pt.isel.ncml.objectivedb.util.cache.LoadingCachePropertiesBuilder;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * All time based properties are set in milliseconds
 */
final class InspectorStorage {

    private final LoadingCache<Class<?>, IClassInspector> cache;
    private static final String PROP_BASE = "InspectorStorage";


    public InspectorStorage() {
        cache = createCache();
    }

    public InspectorStorage(Properties properties){
        cache = createCache(properties);
    }

    private LoadingCache<Class<?>, IClassInspector> createCache(Properties properties) {
        return LoadingCachePropertiesBuilder.INSTANCE.buildCache(PROP_BASE, properties, new Loader());
    }

    private LoadingCache<Class<?>, IClassInspector> createCache() {
        return CacheBuilder.newBuilder().build(new Loader());
    }

    public IClassInspector getInspector(Class<?> clazz) {
        try {
            return cache.get(clazz);
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Class not supported " + clazz.getName(), e);
        }
    }

    private class Loader extends CacheLoader<Class<?>, IClassInspector> {
        @Override
        public IClassInspector load(Class<?> key) throws Exception {
            return new ClassInspector(key);
        }
    }
}
