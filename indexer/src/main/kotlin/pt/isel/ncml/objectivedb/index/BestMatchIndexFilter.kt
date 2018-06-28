package pt.isel.ncml.objectivedb.index

import pt.isel.ncml.objectivedb.util.ByteConverter
import java.io.ByteArrayOutputStream

/**
 * Created by Nuno on 24/06/2017.
 */


interface IndexFilter {
    fun filterField(name:String, value:Any)
    fun immutableIndex(): ByteArray
    fun mutableIndex(): ByteArray
}

/**
 * Constructs the byte array for the mutable and immutable indexes defined in the configuration for the filtered fields.
 * Gives the best match according the filters selected
 */
class BestMatchIndexFilter(private val clazz:Class<*>, indexes: ConfigIndexes) : IndexFilter {

    private val mIdxList = indexes.get(clazz.name).filter { it.mutable }.sortedBy(IndexDefinition::fieldName)
    private val imIdxList = indexes.get(clazz.name).filter { !it.mutable }.sortedBy(IndexDefinition::fieldName)
    private val fieldsFilter: MutableMap<String, Any> = HashMap()


    override fun filterField(name:String, value:Any) {
        fieldsFilter[name] = value
    }

    override fun immutableIndex():ByteArray {
        val classAsBytes = ByteConverter.fromObject(clazz)
        return classAsBytes + getBestMatch(imIdxList, fieldsFilter)
    }

    override fun mutableIndex():ByteArray {
        return getBestMatch(mIdxList, fieldsFilter)
    }

}

/**
 * Validates index definitions against the filtered fields by the user.
 * Returns a byte array that has all the filtered fields that match de index order
 */
private fun getBestMatch(indexes: List<IndexDefinition>, fieldFilter:Map<String, Any>): ByteArray {
    //Assuming all indexes consume the greater known byte size
    val out = ByteArrayOutputStream(indexes.size * ByteConverter.DOUBLE_BYTE_SIZE)
    indexes.forEach {
        val f = fieldFilter[it.fieldName]
        if(null == f)
            return out.toByteArray()
        else
            f.let(ByteConverter::fromObject).also(out::write)
    }
    return out.toByteArray()
}

/**
 * Combines two IndexFilters together and returns the byte array that matches between both.
 */
class CombinerIndexFilter(private val idx1: () -> IndexFilter, private val idx2: () -> IndexFilter) : IndexFilter {

    override fun mutableIndex(): ByteArray {
        return getEqualBytes(idx1().mutableIndex(), idx2().mutableIndex())
    }

    private fun getEqualBytes(idx1 : ByteArray, idx2: ByteArray): ByteArray {
        val size = Math.max(idx1.size, idx2.size)
        val buffer = ByteArray(size)
        for (i in 0..size) {
            if (idx1.size > i && idx2.size > i) {
                if (idx1[i] == idx2[i]) {
                    buffer[i] = idx1[i]
                    continue
                }
            }
            return buffer.copyOfRange(0, i)
        }
        return buffer
    }

    override fun immutableIndex(): ByteArray {
        return getEqualBytes(idx1().immutableIndex(), idx2().immutableIndex())
    }

    override fun filterField(name: String, value: Any) {
        idx1().filterField(name, value)
        idx2().filterField(name, value)
    }
}