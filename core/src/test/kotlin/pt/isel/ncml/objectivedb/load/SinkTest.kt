package pt.isel.ncml.objectivedb.load

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import pt.isel.ncml.objectivedb.SerializeItem
import pt.isel.ncml.objectivedb.storage.DbKey
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.util.*

/**
 * Created by Mario on 2017-05-19.
 */
class SinkTest{

    @Test
    fun testSink(){
        val wagon = mutableListOf<SerializeItem>()
        val sink = Sink(wagon, 10, { })
        val key = DbKey("ola".toByteArray())

        val sinkIntermediate = sink.begin(key, true, Bananas::class.java)
        val bytesDasBananas1 =  "bytesDasBanas1".toByteArray()
        val bytesDasBananas2 =  "bytesDasBanas2".toByteArray()
        sinkIntermediate.sink(bytesDasBananas1)
        sinkIntermediate.sink(bytesDasBananas2)
        sinkIntermediate.end(byteArrayOf(), Optional.empty())

        val pair = wagon.get(0)
        assertEquals(key, pair.key)

        val classNameSize = Arrays.copyOfRange(pair.value.byteStream, 1, 5)
        val nameSize : Int = ByteConverter.getInt(classNameSize)
        val canonicalName = Bananas::class.java.canonicalName
        assertEquals(canonicalName.toByteArray().size, nameSize)


        val offset = 5 + nameSize
        val classNameBytes = Arrays.copyOfRange(pair.value.byteStream, 5, offset)
        val name = String(classNameBytes)
        assertEquals(canonicalName, name)

        val data = Arrays.copyOfRange(pair.value.byteStream, offset, pair.value.byteStream.size)
        val bytesDasBananas = bytesDasBananas1.copyOf(bytesDasBananas1.size + bytesDasBananas2.size)
        System.arraycopy(bytesDasBananas2,0,bytesDasBananas, bytesDasBananas1.size,bytesDasBananas2.size)
        assertArrayEquals(bytesDasBananas, data)
    }

}

class Bananas(){

}