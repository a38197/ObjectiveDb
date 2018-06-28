package pt.isel.ncml.objectivedb.storage

import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.index.IDbIndex
import javax.inject.Qualifier

/**
 * Created by Nuno on 04/06/2017.
 */

/**
 * Persistent storage based on ordered key-value pairs. Typically are faster
 */
interface IPersistentStorage {
    /**
     * Creates a register
     * @param key the key to store
     * @param value the value to store
     */
    fun create(key: IDbIdentity, value: IDbValue)
    /**
     * Updates a register
     * @param key the key to store
     * @param value the value to store
     */
    fun update(key: IDbIdentity, value: IDbValue)
    /**
     * Deletes a register
     * @param key the key that identifies the register to delete
     */
    fun delete(key: IDbIdentity)

    /**
     * Gets the value associated with the key
     * @return the bytes associated with a key, if exist
     */
    fun get(key: IDbIdentity): ISerialValue?
    /**
     * Preforms a batch operation to the applied pairs
     */
    fun batch(pairs: Collection<Pair<KVPair, Operation>>)

    /**
     * Gets all the values whose index match the byte array with a start with predicate
     */
    fun queryIndex(startsWith: ByteArray) : Collection<DbResult>

    /**
     * Generates a new key for the object in parameter
     */
    fun generateKey(obj: Any): IDbIdentity

    /**
     * Generates a key with the byte array as payload
     */
    fun generateKey(keyInBytes: ByteArray): IDbIdentity

    /**
     * Closes any opened database resources
     */
    fun closeDb()

    /**
     * An identifier for the null reference
     */
    val nullRef : IDbIdentity

    /**
     * The size in bytes each reference takes
     */
    val refSize : Int
}

enum class Operation {CREATE, UPDATE, DELETE}


/**
 * Interface for the byte stream to store
 */
interface ISerialValue {
    //byte stream to be stored
    val byteStream: ByteArray
}

/**
 * Interface with a full register to store, including optional indexes
 */
interface IDbValue : ISerialValue {
    val idx:IDbIndex ?
}

/**
 * Annotation for the database filename for Dagger
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DbFileName