package pt.isel.ncml.objectivedb.test.model

/**
* Created by nuno on 3/18/17.
*/

interface ModelEntity

open class PrimitiveEntity(
        var character: Char = ' ',
        var integer: Int = 0,
        var long: Long = 0L,
        var double: Double = 0.0,
        var float: Float = 0.0F
) : ModelEntity

open class ReferenceEntity(var referant:Any?) : ModelEntity {

}

open class ArrayEntity<T>(
        var array: Array<T>
) : ModelEntity

class ReferenceWithPrimitive(referant:Any, val primitive:PrimitiveEntity) : ReferenceEntity(referant)