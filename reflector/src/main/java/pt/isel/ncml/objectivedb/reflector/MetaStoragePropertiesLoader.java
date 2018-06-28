package pt.isel.ncml.objectivedb.reflector;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.inspector.IClassInspector;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;
import pt.isel.ncml.objectivedb.util.DbProperties;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Loads the specified MetaStore from a properties file or uses a default.
 */
public class MetaStoragePropertiesLoader implements IMetaStore {

    private IMetaStore delegate;
    private Logger logger = LoggerFactory.getLogger(MetaStoragePropertiesLoader.class);

    @Inject
    public MetaStoragePropertiesLoader(IObjectResolver resolver, ConfigIndexes indexes) {
        delegate = loadFromProperties(resolver, indexes, DbProperties.INSTANCE::loadReflectorProperties);
    }

    @VisibleForTesting
    MetaStoragePropertiesLoader(IObjectResolver resolver, Properties properties, ConfigIndexes indexes) {
        delegate = loadFromProperties(resolver, indexes, () -> Optional.of(properties));
    }

    /**
     * Gets a meta store first from the properties configuration, or a default one if none configured
     * @param resolver object resolver
     * @param indexes
     *@param propSup obtains the properties. Its a supplier for test purposes  @return
     * @throws IllegalStateException if the meta store instance cannot be created
     */
    private IMetaStore loadFromProperties(IObjectResolver resolver, ConfigIndexes indexes, Supplier<Optional<Properties>> propSup) throws IllegalStateException {
        return propSup.get()
                .map(properties -> createInstance(properties, resolver, indexes))
                .orElseGet(() -> {
                    logger.info("Using default meta store {}", MethodHandlesMetaStorage.class.getName());
                    return new MethodHandlesMetaStorage(resolver, indexes);
                });
    }

    private IMetaStore createInstance(Properties properties, IObjectResolver resolver, ConfigIndexes indexes) throws IllegalStateException {
        logger.info("Creating meta storage from properties file");
        try {
            final String property = properties.getProperty(IMetaStore.META_STORE_IMPL);
            if(null == property){
                logger.debug("Meta store implementation property not found");
                return null;
            }

            logger.debug("Meta store implementation {}", property);
            final Class<?> storeClass = Class.forName(property);
            final Constructor<?> constructor = storeClass.getConstructor(IObjectResolver.class, ConfigIndexes.class);
            return (IMetaStore) constructor.newInstance(resolver, indexes);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot load meta storage from properties", e);
        }
    }

    @Nonnull
    @Override
    public  IFieldStateManipulator getFieldManipulator(Class<?> aClass) throws IllegalArgumentException {
        return  delegate.getFieldManipulator(aClass);
    }

    @Nonnull
    @Override
    public IFieldStateManipulator getArrayManipulator(Class<?> componentType) throws IllegalArgumentException {
        return  delegate.getArrayManipulator(componentType);
    }

    @Nonnull
    @Override
    public  IClassInspector getClassInspector(Class<?> aClass) throws IllegalArgumentException {
        return  delegate.getClassInspector(aClass);
    }

    public IMetaStore getDelegate() {
        return delegate;
    }
}
