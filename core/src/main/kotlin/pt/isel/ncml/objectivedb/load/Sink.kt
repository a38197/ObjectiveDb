package pt.isel.ncml.objectivedb.load

import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.SerializeItem
import pt.isel.ncml.objectivedb.index.DbIndex
import pt.isel.ncml.objectivedb.serialization.IDataSink
import pt.isel.ncml.objectivedb.serialization.IDataSinkIntermediate
import pt.isel.ncml.objectivedb.storage.DbValue
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.*

/**
 * Created by Mario on 2017-05-18.
 */
class Sink
constructor(private val dataList : MutableList<SerializeItem>,
                       private val maxBufferSize:Int,
                       private val clearBuffer: (MutableList<SerializeItem>)->Unit): IDataSink{


    override fun flush() {
        clearBuffer.invoke(dataList)
    }


    override fun begin(dbKey: IDbIdentity, root:Boolean, reflectedClass: Class<*>): IDataSinkIntermediate {
        val data : ByteArrayOutputStream = ByteArrayOutputStream();
        data.write(ByteConverter.fromBoolean(root))
        val nameAsByteArray = reflectedClass.name.toByteArray(Charset.defaultCharset())
        storeClassNAmeSize(data, nameAsByteArray.size)
        data.write(nameAsByteArray)

        return object : IDataSinkIntermediate{
            override fun sink(bytes: ByteArray) {
                data.write(bytes)
            }

            override fun end(imIdx: ByteArray, mutIdx: Optional<ByteArray>): IDataSink {
                dataList.add(SerializeItem(dbKey, DbValue(data.toByteArray(), DbIndex(imIdx, mutIdx.orElse(null)))))
                if(dataList.size >= maxBufferSize){
                    clearBuffer.invoke(dataList)
                }
                return this@Sink
            }
        }
    }

    private fun storeClassNAmeSize(data: ByteArrayOutputStream, nameLength: Int) {
        val byteArray = ByteConverter.fromInt(nameLength)
        data.write(byteArray)
    }
}

