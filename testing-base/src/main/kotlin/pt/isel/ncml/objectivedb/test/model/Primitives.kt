package pt.isel.ncml.objectivedb.test.model

/**
 * Created by nuno on 5/13/17.
 */

class PInt(val value: Int)

class PLong(val value: Long)

class PFloat(val value: Float)

class PDouble(val value: Double)

class PByte(val value: Byte)

class PChar(val value: Char)

class PShort(val value: Short)

class PBoolean(val value: Boolean)

class AllPrimitives(
        val i: Int = 0,
        val l: Long = 0,
        val f: Float = 0F,
        val d: Double = 0.0,
        val b: Byte = 0,
        val c: Char = ' ',
        val s: Short = 0,
        val bl: Boolean = false
)
