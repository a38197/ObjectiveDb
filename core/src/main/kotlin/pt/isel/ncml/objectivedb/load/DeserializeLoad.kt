package pt.isel.ncml.objectivedb.load

import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.IInternalLock
import pt.isel.ncml.objectivedb.identity.IIdentityService
import pt.isel.ncml.objectivedb.serialization.IDataSource
import pt.isel.ncml.objectivedb.serialization.ILoadFactory
import pt.isel.ncml.objectivedb.serialization.ISerializerLoader
import pt.isel.ncml.objectivedb.storage.IPersistentStorage
import pt.isel.ncml.objectivedb.storage.ISerialValue
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.util.*
import javax.inject.Inject

/**
 * Created by Mario on 2017-05-17.
 */

class LoadFactory @Inject constructor(
        private val persistentStorage: IPersistentStorage,
        private val internalLock: IInternalLock,
        private val identity : IIdentityService
): ILoadFactory {

    override fun create(serializer: ISerializerLoader, emptyInstanceMap: MutableList<Pair<IDataSource, Any>>): (ByteArray) ->Any? {
        return Load(persistentStorage, serializer, identity, emptyInstanceMap, internalLock)
    }

}

class Load(private val persistentStorage: IPersistentStorage,
           private val serializer: ISerializerLoader,
           private val identity : IIdentityService,
           private val emptyInstanceMap: MutableList<Pair<IDataSource, Any>>,
           private val internalLock: IInternalLock): (ByteArray) -> Any? {

    override fun invoke(bytes: ByteArray): Any? {
        internalLock.use{
            internalLock.lock()
            val dbIdentity = persistentStorage.generateKey(bytes)
            val optional = identity.getObject(dbIdentity)
            if(optional != null){
                return optional
            }
            return createObject(dbIdentity)
        }
    }

    private val emptyByte = ByteArray(0)

    private fun createObject(dbIdentity: IDbIdentity): Any? {
        val value: ISerialValue? = persistentStorage.get(dbIdentity)
        val source = Source(value?.byteStream ?: emptyByte , dbIdentity)
        return serializer.createEmptyInstanceOrNull(source, emptyInstanceMap)
    }

}

class Source(private val inputStream: ByteArray, private val key: IDbIdentity): IDataSource {

    override fun isRoot(): Boolean {
        return ByteConverter.getBoolean(inputStream, 0)
    }

    private var offset : Int = 0;

    override fun isNull(): Boolean {
        return inputStream.isEmpty()
    }

    override fun getOffset(): Int {
        return offset
    }

    override fun getObjectClass(): Class<*> {
        val classNameSize = Arrays.copyOfRange(inputStream, 1, 5)
        offset = ByteConverter.getInt(classNameSize) + 5
        val classNameAsBytes = Arrays.copyOfRange(inputStream, 5, offset)
        val className = String(classNameAsBytes)
        return Class.forName(className);
    }

    override fun objectBytes(): ByteArray {
        return inputStream
    }

    override fun getDbKey(): IDbIdentity {
        return key
    }

    override fun getObjectSize(): Int {
        return inputStream.size - offset
    }


}
