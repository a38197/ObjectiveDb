package pt.isel.ncml.objectivedb.identity

import org.slf4j.LoggerFactory
import pt.isel.ncml.objectivedb.IDbIdentity
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

/**
 * Created by nuno on 4/16/17.
 */

interface IIdentityCache{

    fun store(dbKey: IDbIdentity, o : Any, root:Boolean)
    fun getObject( key: IRefIdentity) : Any?
    fun getObject( key: IDbIdentity) : Any?
    fun getDbIdentity( o: Any) : IIdentityService.ObjectInfo?
    fun extractRefKey(o :Any): IRefIdentity
    fun remove(dbKey: IDbIdentity)

}

class IdentityCache @Inject constructor(private val keyToObject : ConcurrentMap<IRefIdentity, Any>,
                                        private val objectToDbKey: ConcurrentMap<Any, IIdentityService.ObjectInfo>,
                                        private val dbKeyToObject: ConcurrentMap<IDbIdentity, Any>) : IIdentityCache{
    val log = LoggerFactory.getLogger(IdentityCache::class.java)

    override fun getObject(key: IDbIdentity): Any? {
        return dbKeyToObject.get(key)
    }

    override fun extractRefKey(o: Any): IRefIdentity {
        return RefKey(System.identityHashCode(o))
    }

    override fun getDbIdentity(o: Any): IIdentityService.ObjectInfo? {
        return objectToDbKey.get(o)
    }

    override fun store(dbKey: IDbIdentity, o: Any, root:Boolean) {
        val key = extractRefKey(o)
        if(keyToObject.containsKey(key)){
            log.error("ERROR: generated a repeated key! - {}", key)
            throw InternalError()
        }
        keyToObject.put(key, o)
        objectToDbKey.put(o, IIdentityService.ObjectInfo(dbKey, root))
        dbKeyToObject.put(dbKey, o)
    }

    override fun remove(dbKey: IDbIdentity) {
        val removed = dbKeyToObject.remove(dbKey) ?: return
        objectToDbKey.remove(removed)
        keyToObject.remove(extractRefKey(removed))
    }

    override fun getObject(key: IRefIdentity): Any? {
       return keyToObject.get(key)
    }

}
