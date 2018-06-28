package pt.isel.ncml.objectivedb.serialization.instantiation

import com.google.common.collect.ImmutableMap
import org.objenesis.Objenesis
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.lang.reflect.Array
import java.lang.reflect.Constructor
import java.util.*
import javax.inject.Inject

/**
 * Creates instances from bytes + metadata
 */
interface IInstanceFactory {
    /**
     * @throws InstantiationException if the object could not be crated
     */
    fun createInstance(clazz: Class<*>, objectSize: Int) : Any
}

open class InstantiationException : Exception {
    constructor(msg:String) : super(msg)

    constructor(msg:String, cause:Throwable) : super(msg, cause)
}

/**
 * This instance factory needs a default constructor for each object it creates
 */
class DefaultInstanceFactory @Inject constructor(private val objectResolver: IObjectResolver, private val objectInstantiator: Objenesis) : IInstanceFactory {

    override fun createInstance(clazz: Class<*>, objectSize: Int): Any {
        try {
            if(clazz.componentType != null){
                val optional = Optional.ofNullable(primMap.get(clazz.componentType))
                val size = optional.orElse(objectResolver.referenceSize)
                return Array.newInstance(clazz.componentType, objectSize/size)
            }
            //val optional = getConstructor(clazz)
            //TODO: change this to:
//            val instantiatorOf = objectInstantiator.getInstantiatorOf(clazz)
//            return instantiatorOf.newInstance()
            return objectInstantiator.newInstance(clazz)
        } catch(e: Exception) {
            throw InstantiationException("Could not create ${clazz} object", e)
        }
    }

    private fun getConstructor(clazz: Class<*>): Optional<Constructor<*>> {
        try {
            return Optional.of(clazz.getConstructor()) //Does not throw Error
        }catch (e : NoSuchMethodException){
            return Optional.empty()
        }
    }

    private val primMap = ImmutableMap.builder<Class<*>, Int>()
            .put(Int::class.javaPrimitiveType!!, ByteConverter.INT_BYTE_SIZE)
            .put(Long::class.javaPrimitiveType!!, ByteConverter.LONG_BYTE_SIZE)
            .put(Float::class.javaPrimitiveType!!, ByteConverter.FLOAT_BYTE_SIZE)
            .put(Double::class.javaPrimitiveType!!, ByteConverter.DOUBLE_BYTE_SIZE)
            .put(Byte::class.javaPrimitiveType!!, ByteConverter.BYTE_BYTE_SIZE)
            .put(Char::class.javaPrimitiveType!!, ByteConverter.CHAR_BYTE_SIZE)
            .put(Short::class.javaPrimitiveType!!, ByteConverter.SHORT_BYTE_SIZE)
            .put(Boolean::class.javaPrimitiveType!!, ByteConverter.BOOLEAN_BYTE_SIZE)
            .build()


}