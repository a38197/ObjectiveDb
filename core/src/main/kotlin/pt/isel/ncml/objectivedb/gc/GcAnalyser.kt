package pt.isel.ncml.objectivedb.gc

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.identity.IIdentityService
import pt.isel.ncml.objectivedb.serialization.GcData
import pt.isel.ncml.objectivedb.serialization.IGcAnalyser
import pt.isel.ncml.objectivedb.serialization.IGcAnalyserAdder
import pt.isel.ncml.objectivedb.storage.DbKey
import pt.isel.ncml.objectivedb.storage.DbValue
import pt.isel.ncml.objectivedb.storage.IPersistentStorage
import pt.isel.ncml.objectivedb.storage.ISerialValue
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.util.*

/**
 * Created by Mario on 2017-06-02.
 */
class GcAnalyser(private val storage:IPersistentStorage,
                 private val referenceSize:Int,
                 private val data:MutableMap<IDbIdentity, GcData>,
                 private val identityService: IIdentityService) : IGcAnalyser{


    override fun analyseUnmanage(storedGraph: Map<IDbIdentity, GcData>, identity : IDbIdentity) {
        val rootGcData = storedGraph[identity]!!
        val deletedItems = rootGcData.references.asSequence()
                .map { ref -> Pair(ref, storedGraph[ref]!!) }
                .onEach { pair -> pair.second.isReferencedFrom.remove(identity) }
                .filter { pair -> pair.second.isReferencedFrom.isEmpty() }
                .map { pair -> pair.first }
                .onEach { ref -> deleteMetaInfo(ref)}
                .toCollection(mutableSetOf<IDbIdentity>())
        if(rootGcData.isReferencedFrom.isEmpty()){
            deleteMetaInfo(identity)
            deletedItems.add(identity)
        }
        storedGraph.asSequence()
                .filter { entry -> !deletedItems.contains(entry.key) }
                .forEach { entry -> updateMetaInfo(entry)  }
    }


    override fun referenceAdder(): IGcAnalyserAdder {
        return GcAnalyserAdder(data, mutableSetOf())
    }

    override fun analyseManage(storedInformation: Map<IDbIdentity, GcData>) {
        val itemsToDelete = compareData(storedInformation, data)
        itemsToDelete.forEach { item  ->deleteMetaInfo(item) }
        data.asSequence()
                .filter { entry -> storedInformation.containsKey(entry.key) }
                .forEach { entry -> entry.value.isReferencedFrom.addAll(storedInformation[entry.key]!!.isReferencedFrom) }
        data.forEach{ entry -> updateMetaInfo(entry)}
    }

    private fun updateMetaInfo(entry: Map.Entry<IDbIdentity, GcData>) {
        storage.update(genGcKey(entry.key), DbValue(gcDataToBytes(entry.value), null))
    }

    private fun deleteMetaInfo(item: IDbIdentity) {
        storage.delete(item)
        storage.delete(genGcKey(item))
        identityService.unmanageObject(item)
    }

    private fun gcDataToBytes(value: GcData): ByteArray {
        val byteOutputStream = ByteOutputStream(value.isReferencedFrom.size * referenceSize + value.references.size * referenceSize)
        byteOutputStream.write(ByteConverter.fromInt(value.isReferencedFrom.size))
        byteOutputStream.write(ByteConverter.fromInt(value.references.size))
        value.isReferencedFrom.forEach { ref -> byteOutputStream.write(ref.value) }
        value.references.forEach { ref -> byteOutputStream.write(ref.value) }
        return byteOutputStream.bytes
    }

    private fun compareData(storedInformation: Map<IDbIdentity, GcData>, current: MutableMap<IDbIdentity, GcData>): Collection<IDbIdentity> {
        val gcCandidates = findGcCandidates(current, storedInformation)
        val candidatesToAnalyse = getGcInformation(gcCandidates)
        return candidatesToAnalyse.asSequence()
                .filter { entry -> entry.value.isReferencedFrom.asSequence().all { ref -> current.containsKey(ref) } }
                .map{entry -> entry.key}
                .toCollection(mutableListOf())
    }

    private fun findGcCandidates(current: MutableMap<IDbIdentity, GcData>, storedInformation: Map<IDbIdentity, GcData>): MutableSet<IDbIdentity> {
        return current.asSequence()
                .filter { entry -> !entry.value.references.isEmpty() }
                .filter { entry -> storedInformation.containsKey(entry.key) }
                .map { entry -> Pair(entry.value.references, storedInformation.get(entry.key)!!.references) }
                .map { pair -> findMissingReferences(pair) }
                .flatMap { set -> set.asSequence() }
                .toCollection(mutableSetOf())
    }

    private fun findMissingReferences(pair: Pair<MutableSet<IDbIdentity>, MutableSet<IDbIdentity>>): Set<IDbIdentity> {
        return pair.second.asSequence()
                .filter { ref -> !pair.first.contains(ref) }
                .toCollection(mutableSetOf())
    }

    fun getGcInformation(keysToFetch: Set<IDbIdentity>): Map<IDbIdentity, GcData>{
        return keysToFetch.asSequence()
                .map { key ->
                    val gcKey = genGcKey(key)
                    val second = storage.get(gcKey)
                    Pair(key, decodeData(second))
                }
                .toMap()
    }

    private fun genGcKey(key: IDbIdentity): IDbIdentity {
        val byteOutputStream = ByteOutputStream(referenceSize+GC_KEY_HEADER)
        //add GC header
        byteOutputStream.write("G".toByteArray())
        byteOutputStream.write(key.value)
        val key1 = DbKey(byteOutputStream.bytes)
        return key1
    }

    private fun decodeData(optionalData: ISerialValue?): GcData {
        if(optionalData == null){
            return GcData(mutableSetOf(), mutableSetOf())
        }
        val data = optionalData
        val rootsSize = ByteConverter.getInt(data.byteStream, ROOT_KEYS_QUANTITY_LOCATION)
        val referencesSize = ByteConverter.getInt(data.byteStream, REFERENCE_KEYS_QUANTITY_LOCATION)
        val rootKeys = getKeys(rootsSize, data, DATA_LOCATION)
        val refKeys = getKeys(referencesSize, data, DATA_LOCATION+rootsSize)
        return GcData(rootKeys,refKeys)
    }

    private fun getKeys(size: Int, data: ISerialValue, offset:Int): MutableSet<IDbIdentity>{
        val keysToReturn = mutableSetOf<IDbIdentity>()
        val trueSize = size * referenceSize
        val byteOutputStream = ByteOutputStream(trueSize)
        byteOutputStream.write(data.byteStream, offset, trueSize)
        val bytes = byteOutputStream.bytes
        var pos = 0;
        while(pos<trueSize) {
            val keyBytes = Arrays.copyOfRange(bytes, pos, pos + referenceSize)
            keysToReturn.add(DbKey(keyBytes))
            pos += referenceSize
        }
        return keysToReturn
    }
}

const val GC_KEY_HEADER = 2
const val ROOT_KEYS_QUANTITY_LOCATION = 0
const val REFERENCE_KEYS_QUANTITY_LOCATION = 4
const val DATA_LOCATION = 8

class GcAnalyserAdder(private val data : MutableMap<IDbIdentity, GcData>,
                      private val currentRoots: Set<IDbIdentity>):IGcAnalyserAdder{

    override fun newRoot(root: IDbIdentity): IGcAnalyserAdder {
        val roots = mutableSetOf<IDbIdentity>()
        roots.addAll(currentRoots)
        currentRoots.forEach({r -> data.get(r)!!.references.add(root)})
        data.computeIfAbsent(root, {
            val isReferencedFrom = mutableSetOf<IDbIdentity>()
            isReferencedFrom.addAll(roots)
            GcData(isReferencedFrom, mutableSetOf())
        })
        roots.add(root)
        return GcAnalyserAdder(data, roots)
    }

    override fun addReference(ref: IDbIdentity) : GcAnalyserAdder{
        data.compute(ref,
                {key, gcData ->
                    val dataGc: GcData = gcData ?: GcData(mutableSetOf(), mutableSetOf())
                    updateReferences(key, dataGc)
                    return@compute dataGc
                }
        )
        currentRoots.forEach(
                { root ->
                    val gcData = data.get(root)!! //should be impossible to be null
                    if(root != ref){
                        gcData.references.add(ref)
                    }
                }
        )
        return this@GcAnalyserAdder
    }

    private fun updateReferences(key: IDbIdentity, dataGc: GcData) {
        if (currentRoots.contains(key)) {
            dataGc.references.forEach { r ->
                val ref = data.get(r)!!
                updateReferences(r, ref)
            }
        }
        currentRoots.filter { r -> r != key }
                .forEach { r -> dataGc.isReferencedFrom.add(r) }
    }

}

