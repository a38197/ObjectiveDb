package pt.isel.ncml.objectivedb.reflector;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by nuno on 4/25/17.
 */
public class MetaStoragePropertiesLoaderTest {

    @Test
    public void loadMetaStorageFromProperties() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty(IMetaStore.META_STORE_IMPL, ReflectionMetaStorage.class.getName());

        final MetaStoragePropertiesLoader loader = new MetaStoragePropertiesLoader(null, properties, null);
        assertEquals(ReflectionMetaStorage.class, loader.getDelegate().getClass());
    }

    @Test
    public void testDefaultNoPropertiesFile() throws Exception {
        final MetaStoragePropertiesLoader loader = new MetaStoragePropertiesLoader(null, null);
        assertEquals(MethodHandlesMetaStorage.class, loader.getDelegate().getClass());
    }

    @Test
    public void testDefaultNoPropertyKey() throws Exception {
        final Properties properties = new Properties();

        final MetaStoragePropertiesLoader loader = new MetaStoragePropertiesLoader(null, properties, null);
        assertEquals(MethodHandlesMetaStorage.class, loader.getDelegate().getClass());
    }
}