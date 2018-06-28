package pt.isel.ncml.objectivedb.reflector;

import com.google.common.cache.LoadingCache;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.inspector.IClassInspector;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;
import pt.isel.ncml.objectivedb.util.DbProperties;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static pt.isel.ncml.objectivedb.util.PropertiesKt.REFLECT_PREFIX;

/**
 * Meta storage base with loading cache from guava
 */
abstract class MetaStorageCache implements IMetaStore {

    static final String PROP_BASE = REFLECT_PREFIX + "MetaStorageCache";
    private final LoadingCache<Class<?>, IFieldStateManipulator> manipulatorCache;
    private final InspectorStorage inspectorStorage;

    MetaStorageCache(IObjectResolver resolver, ConfigIndexes indexes) {
        final Optional<Properties> properties = DbProperties.INSTANCE.loadReflectorProperties();
        manipulatorCache = properties
                .map(prop -> createCache(resolver, prop, indexes))
                .orElseGet(() -> createCache(resolver, indexes));

        inspectorStorage = properties
                .map(InspectorStorage::new) //with properties
                .orElseGet(InspectorStorage::new);
    }

    /**
     * Creates a new cache to store the manipulators, <b>is called during object construction</b>. It's not advised but is constrained to package.
     * @param resolver object resolver
     * @param prop properties file for reflector
     * @param indexes
     * @return a new cache from the properies file
     */
    protected abstract LoadingCache<Class<?>, IFieldStateManipulator> createCache(IObjectResolver resolver, Properties prop, ConfigIndexes indexes);

    /**
     * Creates a new cache to store the manipulators, <b>is called during object construction</b>. It's not advised but is constrained to package.
     * @param resolver object resolver
     * @param indexes
     * @return a new cache
     */
    protected abstract LoadingCache<Class<?>, IFieldStateManipulator> createCache(IObjectResolver resolver, ConfigIndexes indexes);

    @Nonnull
    @Override
    public IFieldStateManipulator getFieldManipulator(Class<?> aClass) throws IllegalArgumentException {
        try {
            return manipulatorCache.get(aClass);
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Class is not supported " + aClass, e);
        }
    }

    @Nonnull
    @Override
    public IClassInspector getClassInspector(Class<?> aClass) throws IllegalArgumentException {
        return inspectorStorage.getInspector(aClass);
    }
}
