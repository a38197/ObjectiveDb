package pt.isel.ncml.objectivedb.test.identitystubs

import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.identity.IIdentityService
import pt.isel.ncml.objectivedb.identity.IRefIdentity
import java.util.*

/**
 * Created by Mario on 2017-05-19.
 */
class IdentityServiceStub : IIdentityService {
    override fun unmanageObject(dbKey: IDbIdentity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val map : MutableMap<IDbIdentity, Any> = hashMapOf()
    val inverse : MutableMap<Any, IIdentityService.ObjectInfo> = hashMapOf()

    override fun getRef(dbId: IDbIdentity): IRefIdentity? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDbIdentity(obj: Any): IIdentityService.ObjectInfo? {
        return inverse.get(obj)
    }

    override fun getObject(dbKey: IDbIdentity): Optional<*> {
        return Optional.ofNullable(map.get(dbKey))
    }


    override fun manageObject(dbKey: IDbIdentity, o: Any, root:Boolean) {
        map.put(dbKey, o)
        inverse.put(o, IIdentityService.ObjectInfo(dbKey, root))
    }
}