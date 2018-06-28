package pt.isel.ncml.objectivedb.identity

import pt.isel.ncml.objectivedb.IDbIdentity
import javax.inject.Inject

/**
 * Created by nuno on 4/16/17.
 */

interface IRefIdentity{
    fun getKey() : Int
}

interface IIdentityService {

    fun getRef(dbId: IDbIdentity): IRefIdentity?

    fun getDbIdentity(obj: Any): ObjectInfo?

    fun getObject(dbKey:IDbIdentity) : Any?

    fun manageObject(dbKey: IDbIdentity, o: Any, root:Boolean)

    fun unmanageObject(dbKey: IDbIdentity)

    data class ObjectInfo(val identity:IDbIdentity, val isRoot : Boolean)

}

class IdentityService @Inject constructor(private val cache: IIdentityCache): IIdentityService{

    override fun unmanageObject(dbKey: IDbIdentity) {
        cache.remove(dbKey)
    }

    override fun getObject(dbKey: IDbIdentity): Any? {
        return cache.getObject(dbKey)
    }

    override fun getDbIdentity(obj: Any): IIdentityService.ObjectInfo? {
        return cache.getDbIdentity(obj)
    }

    override fun manageObject(dbKey: IDbIdentity, o: Any, root:Boolean) {
        cache.store(dbKey, o, root)
    }

    override fun getRef(dbId: IDbIdentity): IRefIdentity? {
        val optional = cache.getObject(dbId)
        return optional?.let { cache.extractRefKey(this) }
    }

}

class RefKey(private val key : Int) : IRefIdentity{
    override fun getKey(): Int {
        return key
    }

}
