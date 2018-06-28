package pt.isel.ncml.objectivedb.serialization

import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.reflector.IReflectedObject
import java.util.*

/**
 * Interface used as an entry point to get and put the data to a specific sink
 */
interface ISerializer {
    /**
     * Serializes the object and its dependencies to the sync provided.
     */
    fun serialize(meta: IReflectedObject, sink:IDataSink, gcAnalyserAdder: IGcAnalyserAdder)

    /**
     * Retrieves an object from the data source provided
     */
    fun deserialize(source:IDataSource) : Any?
}

interface ISerializerLoader : ISerializer {
    /**
     * Creates and empty instance to later populated by the deserialize process. This method is necessary to
     * avoid stack overflow on a deep object deserialize
     */
    fun createEmptyInstanceOrNull(source: IDataSource, emptyInstanceMap: MutableList<Pair<IDataSource, Any>>): Any?
}

/**
 * Source for an object to a deserialize process
 */
interface IDataSource {
    fun getObjectClass():Class<*>
    fun objectBytes(): ByteArray
    fun getDbKey() : IDbIdentity
    fun getOffset(): Int
    fun getObjectSize():Int
    fun isNull():Boolean
    fun isRoot(): Boolean

}

/**
 * Represents a repository for the objects in the persistence
 */
interface IDataSink {
    /**
     * prepares the storage of an object
     * @param dbKey key to of the object to be stored
     * @param reflectedClass the object class to store the name on the metadata of the object
     * @return IDataSinkIntermediate to send the objects data
     */
    fun begin(dbKey: IDbIdentity, root:Boolean, reflectedClass: Class<*>): IDataSinkIntermediate

    /**
     * flush must be called after the last usage of the IDataSink, to ensure that all the data was written to the persistence
     */
    fun flush()

}

interface IDataSinkIntermediate{
    /**
     * stores the data of the object
     * @param adds the bytes to the buffer
     */
    fun sink(bytes: ByteArray)

    /**
     * indicates that there is no more data belonging to the object and may initialize a write to the persistence if the buffer is full enough
     */
    fun end(imIdx: ByteArray, mutIdx: Optional<ByteArray>):IDataSink
}


interface ILoadFactory{
    fun create(serializer: ISerializerLoader, emptyInstanceMap: MutableList<Pair<IDataSource, Any>>): (ByteArray) -> Any?
}

interface IGcAnalyser {
    fun referenceAdder():IGcAnalyserAdder
    fun analyseManage(storedInformation: Map<IDbIdentity, GcData>)
    fun analyseUnmanage(storedGraph: Map<IDbIdentity, GcData>, identity : IDbIdentity)
}

interface IGcAnalyserAdder{
    fun newRoot(root:IDbIdentity):IGcAnalyserAdder
    fun addReference(ref:IDbIdentity):IGcAnalyserAdder
}

data class GcData(val isReferencedFrom:MutableSet<IDbIdentity>, val references:MutableSet<IDbIdentity>)