package pt.isel.ncml.objectivedb

import pt.isel.ncml.objectivedb.exception.DbException
import pt.isel.ncml.objectivedb.gc.GcAnalyser
import pt.isel.ncml.objectivedb.identity.IIdentityService
import pt.isel.ncml.objectivedb.load.Sink
import pt.isel.ncml.objectivedb.query.QueryFactory
import pt.isel.ncml.objectivedb.query.UserQuery
import pt.isel.ncml.objectivedb.reflector.IReflector
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver
import pt.isel.ncml.objectivedb.serialization.GcData
import pt.isel.ncml.objectivedb.serialization.ISerializer
import pt.isel.ncml.objectivedb.storage.IDbValue
import pt.isel.ncml.objectivedb.storage.IPersistentStorage
import pt.isel.ncml.objectivedb.storage.KVPair
import pt.isel.ncml.objectivedb.storage.Operation
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by nuno on 3/23/17.
 */

/**
 * Primary interface for user access
 */
interface ObjectiveDB {
    /**
     * Manages the object graph marking the entry point object as Root
     */
    fun manage(obj: Any)

    /**
     * Unamages the object in the parameter. Depending on how many Root exist, and its relationships, this
     * operation may not delete any object. Objects are only deleted when are unreachable by any Root.
     */
    fun unmanage(obj:Any)

    /**
     * Starts the definition of a user query
     */
    fun query(): UserQuery
    /**
     * Closes all resources and releases locks
     */
    fun close()
}

class ObjDbImplement @Inject constructor(
        private val serialization: ISerializer,
        private val reflector: IReflector,
        private val identityService: IIdentityService,
        private val storage: IPersistentStorage,
        private val queries: QueryFactory,
        private val keyGen: IObjectResolver,
        private val lock: ILock
) : ObjectiveDB {

    override fun query(): UserQuery = queries.query()


    override fun unmanage(obj: Any) {
        try {
            lock.beginWrite()
            val objectInfo = identityService.getDbIdentity(obj) ?: throw DbException("Object is not managed!")
            if (!objectInfo.isRoot) {
                throw DbException("Object is not a Root!")
            }
            val gcAnalyser = GcAnalyser(storage, keyGen.referenceSize, mutableMapOf(), identityService)
            val keysToFetch = mutableSetOf(objectInfo.identity)
            val rootInfo = gcAnalyser.getGcInformation(keysToFetch)
            keysToFetch.addAll(rootInfo[objectInfo.identity]!!.references)
            keysToFetch.remove(objectInfo.identity)
            val referencesInfo = gcAnalyser.getGcInformation(keysToFetch)
            val allInfo = HashMap(referencesInfo)
            allInfo.putAll(rootInfo)
            gcAnalyser.analyseUnmanage(allInfo, objectInfo.identity)
        }finally {
            lock.finishWrite()
        }
    }

    override fun manage(obj: Any) {
        try{
            lock.beginWrite()
            val optionalDbIdentity = identityService.getDbIdentity(obj)
            if(optionalDbIdentity == null) {
                manageObject(obj)
            }
            val reflected = reflector.reflect(obj)
            val dataList = ArrayList<SerializeItem>()
            val sink = Sink(dataList, BUFFER_SIZE, this::clearBuffer)
            val currentGraph = mutableMapOf<IDbIdentity, GcData>()
            val gcAnalyser = GcAnalyser(storage, keyGen.referenceSize,currentGraph, identityService)
            serialization.serialize(reflected, sink, gcAnalyser.referenceAdder())
            sink.flush()
            val keysToFetch = extractKeys(currentGraph)
            val storedGraph = gcAnalyser.getGcInformation(keysToFetch)
            gcAnalyser.analyseManage(storedGraph)
        }finally {
            lock.finishWrite()
        }
    }

    private fun extractKeys(currentGraph: MutableMap<IDbIdentity, GcData>) = currentGraph.asSequence().map { entry -> entry.key }.toCollection(mutableSetOf())

    private fun manageObject(obj: Any) {
        val dbIdentity = storage.generateKey(obj)
        identityService.manageObject(dbIdentity, obj, true)
    }

    private fun clearBuffer(buffer : MutableList<SerializeItem>){
        val operations = buffer.map { pair -> Pair(KVPair(pair.key, pair.value), Operation.CREATE) }
                .toCollection(arrayListOf())
        storage.batch(operations)
        buffer.clear()
    }

    override fun close() {
        storage.closeDb()
    }
}

const val BUFFER_SIZE = 1000

data class SerializeItem(val key: IDbIdentity, val value: IDbValue)
