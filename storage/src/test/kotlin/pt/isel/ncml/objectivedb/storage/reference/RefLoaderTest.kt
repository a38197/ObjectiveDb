package pt.isel.ncml.objectivedb.storage.reference

import org.junit.Assert.assertTrue
import org.junit.Test
import pt.isel.ncml.objectivedb.util.DbProperties
import pt.isel.ncml.objectivedb.util.STORAGE_PREFIX
import java.util.*

/**
 * Created by Nuno on 01/06/2017.
 */
class RefLoaderTest {
    @Test
    fun checkInstance() {
        DbProperties.setProperties(null)
        val ref = RefLoader.loadFromProperties()
        assertTrue(ref is IntRefManager)
    }

    @Test
    fun byProperties() {
        val p = Properties()
        p.setProperty(STORAGE_PREFIX + KEY_GEN_PROP, LongRefManager::class.java.name)
        DbProperties.setProperties(p)

        val ref = RefLoader.loadFromProperties()
        assertTrue(ref is LongRefManager)
    }
}