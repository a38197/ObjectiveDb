package pt.isel.ncml.objectivedb.util

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by nuno on 4/25/17.
 */
class PropertyAccessTest {

    @Test
    fun loadDefaultPropertiesFile() {
        val loadReflectorProperties = DbProperties.loadReflectorProperties()
        assertTrue(loadReflectorProperties.isPresent)
    }

}