package pt.isel.ncml.objectivedb.storage

import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.*
import org.slf4j.LoggerFactory
import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.storage.reference.IRefManager
import pt.isel.ncml.objectivedb.storage.reference.RefLoader
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.io.*
import java.util.*
import javax.inject.Inject

class PersistentStorage constructor(
        private val fileName : String,
        referenceManager : IRefManager
):IPersistentStorage {

    private val leveldb = createDatabase()
    private val writeOpt = WriteOptions().sync(true)
    private val readOpt = ReadOptions()
    private val ref : IRefManager
    private val configurationKey = ByteConverter.fromString("SportLisboaBenfica")

    @Inject constructor(@DbFileName fileName: String) : this(fileName, RefLoader.REF)

    init {
        val bytes = leveldb.get(configurationKey)
        if(bytes != null){
            val objectInputStream = ObjectInputStream(ByteArrayInputStream(bytes))
            ref = objectInputStream.readObject() as IRefManager
        }else{
            ref = referenceManager
        }
    }

    private fun createDatabase(): DB {
        val file = File(fileName)
        val opt = Options()
        opt.createIfMissing(true)
        return JniDBFactory.factory.open(file, opt)
    }

    override fun generateKey(obj: Any): IDbIdentity {
        return DbKey(ref.getNewKey(obj))
    }

    override fun generateKey(keyInBytes: ByteArray): IDbIdentity {
        return DbKey(keyInBytes)
    }


    override fun create(key: IDbIdentity, value: IDbValue) {
        leveldb.put(key.value, value.byteStream, writeOpt)
        putIndex(key, value, leveldb::put)
    }

    private fun putIndex(key: IDbIdentity, value: IDbValue, consumer: (ByteArray, ByteArray)->Unit) {
        val imIdx = value.idx?.immutableIndex
        if(imIdx != null){
            //Composite key so it does not duplicate
            //without complete knowledge of the index I must add the key to the value also so it can be safely extracted
            consumer(imIdx.plus(key.value), key.value)
        }
    }

    override fun update(key: IDbIdentity, value: IDbValue) {
        leveldb.put(key.value, value.byteStream, writeOpt)
    }

    override fun delete(key: IDbIdentity) {
        leveldb.delete(key.value, writeOpt)
    }

    override fun get(key: IDbIdentity): ISerialValue? {
        val bytes = leveldb.get(key.value, readOpt)
        if(null == bytes)
            return null
        else
            return SerializedDbValue(bytes)
    }

    override fun batch(pairs: Collection<Pair<KVPair, Operation>>) {
        val writeBatch = leveldb.createWriteBatch()
        pairs.forEach { pair ->
            if (isWriteOperation(pair.second)) {
                writeBatch.put(pair.first.key.value, pair.first.value.byteStream)
                putIndex(pair.first.key, pair.first.value, { idx, v -> writeBatch.put(idx, v) })
            } else {
                writeBatch.delete(pair.first.key.value)
            }
        }
        leveldb.write(writeBatch, writeOpt)
        writeBatch.close()
    }

    private fun isWriteOperation(oper: Operation): Boolean {
        when (oper) {
            Operation.CREATE -> {
                return true
            }
            Operation.UPDATE -> {
                return true
            }
            Operation.DELETE -> {
                return false
            }
        }
    }

    private fun getKeysStartWith(classNameIdx: ByteArray): Collection<IDbIdentity> {
        val iterator: DBIterator = leveldb.iterator(readOpt)
        val ret = mutableListOf<IDbIdentity>()
        iterator.use {
            iterator.seek(classNameIdx)
            while (iterator.hasNext()) {
                val curr = iterator.next()
                if(curr.key.startsWith(classNameIdx)){
                    ret.add(DbKey(curr.value))
                } else {
                    break //keys are ordered
                }
            }
        }
        return ret
    }

    fun batchRead(keys:Sequence<IDbIdentity>) : Collection<DbResult> {
        val iterator: DBIterator = leveldb.iterator(readOpt)
        val ret = mutableListOf<DbResult>()
        iterator.use {
            keys.forEach {
                iterator.seek(it.value)
                if(!iterator.hasNext())
                    return@forEach

                val data = iterator.next()
                if(Arrays.equals(data?.key, it.value))
                    ret.add(DbResult(it, SerializedDbValue(data.value)))
            }
        }
        return ret
    }

    override fun queryIndex(startsWith: ByteArray): Collection<DbResult> {
        val keys : Collection<IDbIdentity> = getKeysStartWith(startsWith)
        val batchRead = batchRead(keys.asSequence())
        return batchRead
    }

    /**
     * @throws java.io.IOException
     */
    override fun closeDb() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val writer = ObjectOutputStream(byteArrayOutputStream)
        writer.writeObject(ref)
        writer.close()
        leveldb.put(configurationKey, byteArrayOutputStream.toByteArray())
        leveldb.close()
    }

    private val _nullRef = DbKey(ref.nullRef)
    override val nullRef: IDbIdentity
        get() = _nullRef

    override val refSize: Int
        get() = ref.refSize

    private companion object {
        val LOGGER = LoggerFactory.getLogger(PersistentStorage::class.java)
    }
}

private fun ByteArray.startsWith(other:ByteArray) : Boolean {
    val length = if(this.size < other.size) this.size else other.size
    for (i: Int in 0 .. length - 1)
        if(other[i] != this[i])
            return false

    return true;
}
