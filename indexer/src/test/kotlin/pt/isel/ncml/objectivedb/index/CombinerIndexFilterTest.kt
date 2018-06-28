package pt.isel.ncml.objectivedb.index

import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.AllPrimitives
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.util.*

/**
 * Created by Nuno on 25/06/2017.
 */
class CombinerIndexFilterTest {
    @Test
    fun testCombineSameFields() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("f", true))
        indexes.put(name, IndexDefinition("l", true))

        val idx1 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        val combinerIndex = CombinerIndexFilter({idx1}, {idx1})
        idx1.filterField("f", 3F)
        idx1.filterField("l", 3L)


        val expected = ByteConverter.fromFloat(3F) + ByteConverter.fromLong(3L)
        val index = combinerIndex.mutableIndex()
        assert(Arrays.equals(expected, index))
    }

    @Test
    fun testCombinePartialFields() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("f", true))
        indexes.put(name, IndexDefinition("l", true))

        val idx1 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        idx1.filterField("f", 3F)
        idx1.filterField("l", 3L)
        val idx2 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        idx2.filterField("f", 3F)
        val combinerIndex = CombinerIndexFilter({idx1}, {idx2})


        val expected = ByteConverter.fromFloat(3F)
        val index = combinerIndex.mutableIndex()
        assert(Arrays.equals(expected, index))
    }

    @Test
    fun testCombinePartialMatch() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("s", true))

        val idx1 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        idx1.filterField("s", 0b1111111100000000.toShort())
        val idx2 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        idx2.filterField("s", 0b1111111111111111.toShort())
        val combinerIndex = CombinerIndexFilter({idx1}, {idx2})


        val expected = ByteArray(1)
        expected[0] = (0b11111111).toByte()
        val index = combinerIndex.mutableIndex()
        assert(Arrays.equals(expected, index))
    }

    @Test
    fun noMatch() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("s", true))

        val idx1 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        idx1.filterField("s", 0b1110111100000000.toShort())
        val idx2 = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        idx2.filterField("s", 0b1111111100000000.toShort())
        val combinerIndex = CombinerIndexFilter({idx1}, {idx2})


        val expected = ByteArray(0)
        val index = combinerIndex.mutableIndex()
        assert(Arrays.equals(expected, index))
    }
}