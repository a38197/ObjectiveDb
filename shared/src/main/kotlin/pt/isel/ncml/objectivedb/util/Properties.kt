package pt.isel.ncml.objectivedb.util

import com.google.common.annotations.VisibleForTesting
import java.io.InputStream
import java.util.*

const val REFLECT_PREFIX = "reflector."
const val STORAGE_PREFIX = "storage."
private const val PROPERTIES_FILE_NAME = "objectivedb.properties"

/**
 * Object for property load and public access
 */
internal class PropertyAccess(fileName: String) : IDbProperties {

    private var dbProp : Properties? = loadDbProperties(fileName)

    private fun loadDbProperties(fileName: String): Properties? {
        val propStream: InputStream? = javaClass.classLoader.getResourceAsStream(fileName)
        if(null != propStream){
            val properties = Properties()
            properties.load(propStream)
            return properties
        }
        return null
    }

    override fun loadReflectorProperties() : Optional<Properties> {
        return Optional.ofNullable(filterPrefix(REFLECT_PREFIX))
    }

    override fun loadStorageProperties(): Optional<Properties> {
        return Optional.ofNullable(filterPrefix(STORAGE_PREFIX))
    }

    private fun filterPrefix(prefix: String): Properties? {
        return dbProp?.propertyNames()
                ?.let { Sequence( {
                    @Suppress("UNCHECKED_CAST")
                    (it as Enumeration<String>).iterator()
                }   ) } //Turns to a Sequence for more transforming methods
                ?.filter { it.startsWith(prefix) }
                ?.toProperties(prefix){
                    dbProp!!.getProperty(it)
                }
    }

    override fun setProperties(properties: Properties?) {
        dbProp = properties
    }
}

/**
 * Entity for centralized access to properties file.
 * The properties returned are already have the prefix key for the component removed.
 */
interface IDbProperties {
    /**
     * Returns a [Properties] object with the keys that match [REFLECT_PREFIX]. Modifies its keys to remove the prefix.
     */
    fun loadReflectorProperties() : Optional<Properties>

    /**
     * Returns a [Properties] object with the keys that match [STORAGE_PREFIX]. Modifies its keys to remove the prefix.
     */
    fun loadStorageProperties() : Optional<Properties>

    /**
     * Resets the properties at runtime.
     */
    @VisibleForTesting
    fun setProperties(properties: Properties?)
}

/**
 * Singleton object for objectivedb.properties file
 */
object DbProperties : IDbProperties by PropertyAccess(PROPERTIES_FILE_NAME)

/**
 * Sequence can iterate only once
 */
private fun Sequence<String>.toProperties(prefixToRemove: String, values : (String) -> String) : Properties? {
    val toReturn = Properties()
    val list = this.toList()
    list.forEach { toReturn.put(it.replaceFirst(prefixToRemove, ""), values(it)) }
    return if(list.isEmpty()) null else toReturn
}