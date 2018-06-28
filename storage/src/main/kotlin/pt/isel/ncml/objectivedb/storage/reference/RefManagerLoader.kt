package pt.isel.ncml.objectivedb.storage.reference

import com.google.common.annotations.VisibleForTesting
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.ncml.objectivedb.util.DbProperties
import pt.isel.ncml.objectivedb.util.reflection.ReflectionUtils

/**
 * Created by Nuno on 01/06/2017.
 */

internal object RefLoader {

    private val LOGGER : Logger = LoggerFactory.getLogger(RefLoader::class.java)
    val REF :IRefManager = loadFromProperties()

    @VisibleForTesting
    internal fun loadFromProperties(): IRefManager {
        return DbProperties.loadStorageProperties()
                .map {
                    getFromProperty(it.getProperty(KEY_GEN_PROP) ?: DEFAULT_KEY_GEN)
                }
                .orElseGet({ getFromProperty(DEFAULT_KEY_GEN) })
    }

    private fun getFromProperty(property: String): IRefManager {
        LOGGER.debug("Creating key gen instance $property")
        return ReflectionUtils.createInstance(Class.forName(property)) as IRefManager
    }
}
