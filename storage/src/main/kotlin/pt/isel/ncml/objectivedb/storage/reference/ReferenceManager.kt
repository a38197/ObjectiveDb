package pt.isel.ncml.objectivedb.storage.reference

import pt.isel.ncml.objectivedb.util.ByteConverter
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Nuno on 01/06/2017.
 */

/**
 * <p>The property key that must have a class name of the implementation to use.</p>
 * <p>Available values are {@link pt.isel.ncml.objectivedb.reflector.MethodHandlesMetaStorage}</p>
 */
const val KEY_GEN_PROP = "keygen.implementation";
val DEFAULT_KEY_GEN = IntRefManager::class.java.name

/**
 * Interface for reference management for persistence module.
 */
interface IRefManager : Serializable {
    /**
     * Specifies a known, unique value for representing a null reference
     */
    val nullRef : ByteArray
    /**
     * Specified the reference size in bytes
     */
    val refSize : Int

    /**
     * Obtains a persistence key for the given object.
     * It's not mandatory to obtain the same key if the same object is passed as parameter.
     */
    fun getNewKey(obj:Any):ByteArray
}

internal class IntRefManager : IRefManager {

    private val keyGen = AtomicInteger(Int.MIN_VALUE + 1)

    override val refSize: Int
        get() = ByteConverter.INT_BYTE_SIZE

    override fun getNewKey(obj: Any): ByteArray {
        return ByteConverter.fromInt(keyGen.getAndIncrement())
    }

    override val nullRef = ByteConverter.fromInt(Int.MIN_VALUE)
}

internal class LongRefManager : IRefManager {

    private val keyGen = AtomicInteger(Int.MIN_VALUE + 1)

    override val refSize: Int
        get() = ByteConverter.INT_BYTE_SIZE

    override fun getNewKey(obj: Any): ByteArray {
        return ByteConverter.fromInt(keyGen.getAndIncrement())
    }

    override val nullRef: ByteArray = ByteConverter.fromLong(Long.MIN_VALUE)
}