package pt.isel.ncml.objectivedb.index

import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.AllPrimitives
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.util.*

/**
 * Created by Nuno on 25/06/2017.
 */
class BestMatchIndexFilterTest {

    @Test
    fun bestMatch() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("f", false))
        indexes.put(name, IndexDefinition("l", false))
        indexes.put(name, IndexDefinition("i", false))

        val bestMatchIndex = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        bestMatchIndex.filterField("l", 3L)
        bestMatchIndex.filterField("s", 5.toShort())
        bestMatchIndex.filterField("f", 15F)

        val expected = ByteConverter.fromObject(AllPrimitives::class.java) +
                ByteConverter.fromFloat(15F)
        val immutableIndex = bestMatchIndex.immutableIndex()
        assert(Arrays.equals(expected, immutableIndex), expected::contentToString)
    }

    @Test
    fun noneMatch() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("f", true))
        indexes.put(name, IndexDefinition("l", true))
        indexes.put(name, IndexDefinition("i", true))

        val bestMatchIndex = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        bestMatchIndex.filterField("l", 3L)

        val expected = ByteArray(0)
        val index = bestMatchIndex.mutableIndex()
        assert(Arrays.equals(expected, index), expected::contentToString)
    }

    @Test
    fun matchesAll() {
        val indexes = ConfigIndexes()
        val name = AllPrimitives::class.java.name
        indexes.put(name, IndexDefinition("f", false))
        indexes.put(name, IndexDefinition("s", false))
        indexes.put(name, IndexDefinition("l", false))

        val bestMatchIndex = BestMatchIndexFilter(AllPrimitives::class.java, indexes)
        bestMatchIndex.filterField("l", 3L)
        bestMatchIndex.filterField("s", 5.toShort())
        bestMatchIndex.filterField("f", 15F)

        val expected = ByteConverter.fromObject(AllPrimitives::class.java) +
                ByteConverter.fromFloat(15F) + ByteConverter.fromLong(3L) + ByteConverter.fromShort(5)
        val immutableIndex = bestMatchIndex.immutableIndex()
        assert(Arrays.equals(expected, immutableIndex), expected::contentToString)
    }
}