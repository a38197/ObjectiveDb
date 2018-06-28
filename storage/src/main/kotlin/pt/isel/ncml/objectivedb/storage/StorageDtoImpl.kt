package pt.isel.ncml.objectivedb.storage

import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.index.IDbIndex
import java.util.*

/**
 * Created by Nuno on 04/06/2017.
 */

class DbKey (override val value: ByteArray) : IDbIdentity{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || other.javaClass != javaClass) return false

        other as DbKey

        if (!Arrays.equals(value, other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(value)
    }

    override fun toString(): String {
        return value.contentToString()
    }
}

class DbValue(override val byteStream: ByteArray, override val idx: IDbIndex?) : IDbValue {
    override fun toString(): String {
        return "byteStream=${byteStream.contentToString()}," +
            "imIdx=${idx?.immutableIndex?.contentToString()}, mIdx=${idx?.mutableIndex?.contentToString()}"
    }
}
data class SerializedDbValue(override val byteStream: ByteArray) : ISerialValue

data class KVPair(val key: IDbIdentity, val value: IDbValue)
data class OperationResult(val idStart:Int, val idEnd:Int, val affected:Int)
data class DbResult(val key:IDbIdentity, val value:ISerialValue)