package pt.isel.ncml.objectivedb.serialization

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.objenesis.ObjenesisStd
import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.InternalLock
import pt.isel.ncml.objectivedb.reference.IReferenceHandler
import pt.isel.ncml.objectivedb.reflector.IReflectedObject
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver
import pt.isel.ncml.objectivedb.reflector.manipulator.IStateInfo
import pt.isel.ncml.objectivedb.serialization.instantiation.DefaultInstanceFactory
import pt.isel.ncml.objectivedb.test.identitystubs.IdentityServiceStub
import pt.isel.ncml.objectivedb.test.reflectorstubs.ReflectorStub
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by Mario on 2017-05-19.
 */
class SerializerTest{

    @Test
    fun testSerializer(){
        val identityServiceStub = IdentityServiceStub()
        val reflectorStub = ReflectorStub()
        val objectResolver: IReferenceHandler = object : IReferenceHandler {
            override val referenceSize: Int
                get() = 0

        }

        val factory: ILoadFactory = object : ILoadFactory {
            override fun create(serializer: ISerializerLoader, emptyInstanceMap: MutableList<Pair<IDataSource, Any>>): (ByteArray) -> Any? {
                return { String() }
            }

        }

        val batatas = Batatas()
        val dbIdentity: IDbIdentity = DbKey("1".toByteArray())
        identityServiceStub.manageObject(dbIdentity, batatas, true)
        val serializer = Serializer(reflectorStub, identityServiceStub, objectResolver, factory, DefaultInstanceFactory(object : IObjectResolver{
            override fun toByteArray(obj: Any?): ByteArray {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override val referenceSize: Int = 4

        }, ObjenesisStd()), InternalLock(ReentrantLock()), object :IStoreFilter{})


        val reflected: IReflectedObject = object : IReflectedObject {
            override fun getReflectedClass(): Class<*> {
                return batatas.javaClass
            }

            override fun fieldManipulator(): IFieldStateManipulator {
                return object : IFieldStateManipulator{
                    override fun getStateInfo(instance: Any?): IStateInfo {
                        return MyStateInfo();
                    }

                    override fun setStateInfo(instance: Any?, info: IStateInfo?) {
                    }

                }
            }

            override fun getObject(): Any {
                return batatas
            }

            override fun innerReflectedObjects(): MutableIterable<IReflectedObject> {
                return mutableListOf()
            }

        }
        var byte : ByteArray = "".toByteArray();
        val sink: IDataSink = object : IDataSink {
            override fun flush() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun begin(dbKey: IDbIdentity, root:Boolean, reflectedClass: Class<*>): IDataSinkIntermediate {
                return object : IDataSinkIntermediate {
                    override fun sink(bytes: ByteArray) {
                        byte = bytes
                    }

                    override fun end(imIdx: ByteArray, mutIdx: Optional<ByteArray>): IDataSink {
                        return object : IDataSink{
                            override fun flush() {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun begin(dbKey: IDbIdentity, root:Boolean, reflectedClass: Class<*>): IDataSinkIntermediate {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        }
                    }
                }
            }

        }

        serializer.serialize(reflected, sink, object:IGcAnalyserAdder{
            override fun newRoot(root: IDbIdentity): IGcAnalyserAdder {
                return this
            }

            override fun addReference(ref: IDbIdentity):IGcAnalyserAdder {
                return this
            }
        })

        assertArrayEquals("batatas".toByteArray(), byte)
    }
}

class Batatas(){
    val cenas = 34
}

class MyStateInfo:IStateInfo{
    override fun immutableIndexes(): ByteArray {
        return byteArrayOf()
    }

    override fun mutableIndexes(): Optional<ByteArray> {
        return Optional.empty()
    }

    override fun getClazz(): Class<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInt(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLong(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFloat(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDouble(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChar(): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getShort(): Short {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByte(): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoolean(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getObject(): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun asBytes(): ByteArray {
        return "batatas".toByteArray()
    }

}

class DbKey(override val value: ByteArray):IDbIdentity{

}