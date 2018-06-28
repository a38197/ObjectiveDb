package pt.isel.ncml.objectivedb.reflector;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;
import pt.isel.ncml.objectivedb.reflector.manipulator.ReflectionFactory;
import pt.isel.ncml.objectivedb.reflector.manipulator.array.*;
import pt.isel.ncml.objectivedb.util.cache.LoadingCachePropertiesBuilder;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Properties;

/**
 * Blocking version of meta storage with time based cache. Will retreive manipulator based on MethodHandles
 */
class ReflectionMetaStorage extends MetaStorageCache {

    private final IFieldStateManipulator objectArrayManipulator;
    private static final Map<Class<?>, IFieldStateManipulator> arrayManipulators = ImmutableMap
            .<Class<?>, IFieldStateManipulator>builder()
            .put(int.class, new IntArrayManipulator())
            .put(long.class, new LongArrayManipulator())
            .put(float.class, new FloatArrayManipulator())
            .put(double.class, new DoubleArrayManipulator())
            .put(boolean.class, new BooleanArrayManipulator())
            .put(char.class, new CharArrayManipulator())
            .put(byte.class, new ByteArrayManipulator())
            .put(short.class, new ShortArrayManipulator())
            .build();

    public ReflectionMetaStorage(IObjectResolver resolver, ConfigIndexes indexes) {
        super(resolver, indexes);
        objectArrayManipulator = new ObjectArrayManipulator(resolver);
    }

    @Override
    protected LoadingCache<Class<?>, IFieldStateManipulator> createCache(IObjectResolver resolver, Properties prop, ConfigIndexes indexes) {
        return fromProperties(prop, resolver, indexes);
    }

    @Override
    protected LoadingCache<Class<?>, IFieldStateManipulator> createCache(IObjectResolver resolver, ConfigIndexes indexes) {
        return CacheBuilder.newBuilder().build(new Loader(resolver, indexes));
    }

    private LoadingCache<Class<?>, IFieldStateManipulator> fromProperties(Properties properties, IObjectResolver resolver, ConfigIndexes indexes) {
        return LoadingCachePropertiesBuilder.INSTANCE.buildCache(PROP_BASE, properties, new Loader(resolver, indexes));
    }

    @Nonnull
    @Override
    public IFieldStateManipulator getArrayManipulator(Class<?> componentType) throws IllegalArgumentException {
        final IFieldStateManipulator manipulator = arrayManipulators.get(componentType);
        if(null == manipulator)
            return objectArrayManipulator;

        return manipulator;
    }


    private static class Loader extends CacheLoader<Class<?>, IFieldStateManipulator> {

        private final ReflectionFactory reflectionFactory;

        public Loader(IObjectResolver resolver, ConfigIndexes indexes) {
            reflectionFactory = new ReflectionFactory(resolver, indexes);
        }

        @Override
        public IFieldStateManipulator load(Class<?> key) throws ManipulatorException {
            if(key.getComponentType() == null){
                return reflectionFactory.getFieldManipulator(key);
            }
            throw new ManipulatorException("Array manipulators are not loaded to main cache!!!");
        }

    }
}
