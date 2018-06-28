package pt.isel.ncml.objectivedb.serialization

import org.slf4j.LoggerFactory
import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.IInternalLock
import pt.isel.ncml.objectivedb.exception.DbError
import pt.isel.ncml.objectivedb.identity.IIdentityService
import pt.isel.ncml.objectivedb.reference.IReferenceHandler
import pt.isel.ncml.objectivedb.reflector.IReflectedObject
import pt.isel.ncml.objectivedb.reflector.IReflector
import pt.isel.ncml.objectivedb.reflector.manipulator.ObjectFromBytesStateInfo
import pt.isel.ncml.objectivedb.serialization.instantiation.IInstanceFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by mlourenc on 4/18/2017.
 */
class Serializer @Inject constructor(private val reflector : IReflector,
                                     private val identity: IIdentityService,
                                     private val refHandler: IReferenceHandler,
                                     private val loadFactory: ILoadFactory,
                                     private val instanceFactory: IInstanceFactory,
                                     private val internalLock: IInternalLock,
                                     private val storeFilter: IStoreFilter) : ISerializerLoader {

    val log = LoggerFactory.getLogger(Serializer::class.java)


    override fun serialize(meta: IReflectedObject, sink: IDataSink, gcAnalyserAdder: IGcAnalyserAdder) {
        val serializedObjects = HashSet<IDbIdentity>()
        val (currentGcAnalyserAdder, reflectedObjects) = innerSerialize(meta, sink, serializedObjects, gcAnalyserAdder)
        var list = mutableListOf(Pair(currentGcAnalyserAdder, reflectedObjects))
        while(!list.isEmpty()) {
            list = list.asSequence()
                    .map { pair -> Pair( pair.first, filterNullReflectedObjects(pair)) }
                    .map { pair -> serializeReflectedObjects(sink, serializedObjects, pair.second, pair.first) }
                    .flatMap {a -> a.asSequence()}
                    .toCollection(mutableListOf())

        }
    }

    private fun serializeReflectedObjects(sink: IDataSink, serializedObjects: HashSet<IDbIdentity>, reflectedObjects: MutableList<IReflectedObject>, gcAdder: IGcAnalyserAdder) = reflectedObjects.asSequence()
            .map { ro -> innerSerialize(ro, sink, serializedObjects, gcAdder) }

    private fun filterNullReflectedObjects(pair: Pair<IGcAnalyserAdder, Iterable<IReflectedObject>>): MutableList<IReflectedObject> {
        return pair.second.asSequence()
                .filter { ro -> Objects.nonNull(ro) }
                .toCollection(mutableListOf<IReflectedObject>())
    }

    private fun innerSerialize(meta: IReflectedObject,
                               sink: IDataSink, serializedObjects: MutableSet<IDbIdentity>,
                               gcAnalyserAdder: IGcAnalyserAdder): Pair<IGcAnalyserAdder,Iterable<IReflectedObject>> {
        if(meta.isNull){
            return Pair(gcAnalyserAdder,Collections.emptyList())
        }
        val element = meta.`object`
        if(!storeFilter.canBeStored(element)){
            return Pair(gcAnalyserAdder,Collections.emptyList())
        }
        val (dbIdentity, isRoot) = getDbIdentityFromCache(element)
        val currentGcAnalyser = if (isRoot) {
            gcAnalyserAdder.newRoot(dbIdentity)
        } else {
            gcAnalyserAdder.addReference(dbIdentity)
        }
        if(serializedObjects.contains(dbIdentity)){
            //avoid serialize the same object multiple times
            return Pair(currentGcAnalyser,Collections.emptyList())
        }
        serializedObjects.add(dbIdentity)
        val sinkIntermediate = sink.begin(dbIdentity, isRoot, meta.reflectedClass)
        val stateInfo = meta.fieldManipulator().getStateInfo(element)
        sinkIntermediate.sink(stateInfo.asBytes())
        sinkIntermediate.end(stateInfo.immutableIndexes(), stateInfo.mutableIndexes())
        return Pair(currentGcAnalyser,meta.innerReflectedObjects())
    }

    private fun getDbIdentityFromCache(element: Any): IIdentityService.ObjectInfo {
        val optionalDdbIdentity = identity.getDbIdentity(element) ?: throw DbError("this object should have been managed on IObjectResolver")
        return optionalDdbIdentity
    }

    override fun deserialize(source: IDataSource): Any? {
        if(source.isNull()){
            return null;
        }
        val dbKeyBytes = source.getDbKey()

        val newInstance:Any
        val emptyInstanceMap = mutableListOf<Pair<IDataSource, Any>>()
        try {
            internalLock.lock()

            val optional = identity.getObject(dbKeyBytes)
            if (optional != null) {
                return optional
            }
            newInstance = createEmptyInstance(source, emptyInstanceMap)
        }catch(e:Exception){
            throw e
        }finally {
            internalLock.unlock()
        }

        val objectLoader = loadFactory.create(this, emptyInstanceMap)
        do {
            populateEmptyInstance(emptyInstanceMap, objectLoader)
        }while(!emptyInstanceMap.isEmpty())
        return newInstance
    }

    private fun populateEmptyInstance(emptyInstanceList: MutableList<Pair<IDataSource, Any>>, objectLoader: (ByteArray) -> Any?) {
        val pair = emptyInstanceList.first()
        log.debug("populating empty obj {}", pair.first.getObjectClass().name)
        val objectClass = pair.first.getObjectClass()
        val fieldManipulator = reflector.getFieldManipulator(objectClass)
        val objectFromBytesStateInfo = ObjectFromBytesStateInfo(pair.first.objectBytes(), pair.first.getOffset(), objectClass, objectLoader, refHandler.referenceSize)
        fieldManipulator.setStateInfo(pair.second, objectFromBytesStateInfo)
        emptyInstanceList.remove(pair)
    }

    override fun createEmptyInstanceOrNull(source: IDataSource, emptyInstanceMap:MutableList<Pair<IDataSource, Any>>): Any? {
        if(source.isNull()){
            return null
        }
        return createEmptyInstance(source, emptyInstanceMap)
    }

    private fun createEmptyInstance(source: IDataSource, emptyInstanceMap: MutableList<Pair<IDataSource, Any>>): Any {
        log.debug("Creating empty obj {}", source.getObjectClass().name)
        val emptyInstance = instanceFactory.createInstance(source.getObjectClass(), source.getObjectSize())
        identity.manageObject(source.getDbKey(), emptyInstance, source.isRoot())
        emptyInstanceMap.add(Pair(source, emptyInstance))
        return emptyInstance
    }
}
