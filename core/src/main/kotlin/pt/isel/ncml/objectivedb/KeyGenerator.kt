package pt.isel.ncml.objectivedb

import pt.isel.ncml.objectivedb.identity.IIdentityService
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver
import pt.isel.ncml.objectivedb.storage.IPersistentStorage
import javax.inject.Inject

/**
 * Created by Mario on 2017-05-08.
 */
class KeyGenerator @Inject constructor(
        private val persistentStorage: IPersistentStorage,
        private val identityService: IIdentityService,
        private val internalLock: IInternalLock
): IObjectResolver{

    override fun toByteArray(obj: Any?): ByteArray {
        internalLock.use {
            internalLock.lock()
            if(obj == null){
                return persistentStorage.nullRef.value
            }
            val optionalDbIdentity = identityService.getDbIdentity(obj)
            val objectInfo = optionalDbIdentity ?: IIdentityService.ObjectInfo(persistentStorage.generateKey(obj), false) //new object while serializing root, so it is not a root
            identityService.manageObject(objectInfo.identity, obj, objectInfo.isRoot)
            return objectInfo.identity.value
        }
    }

    override val referenceSize: Int get() = persistentStorage.refSize
}