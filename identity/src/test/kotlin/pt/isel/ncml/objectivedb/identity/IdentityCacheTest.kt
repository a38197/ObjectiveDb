package pt.isel.ncml.objectivedb.identity

import com.google.common.collect.MapMaker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pt.isel.ncml.objectivedb.IDbIdentity
import java.util.*

/**
 * Created by Mario on 2017-04-25.
 */
class IdentityCacheTest{

    @Test
    fun basicCacheTest(){
        val identityCache = IdentityCache(MapMaker().weakValues().makeMap(),  MapMaker().weakKeys().makeMap(), MapMaker().weakValues().makeMap())
        val s = "adeus"
        val dbKey = PersistentKey(byteArrayOf(1))
        identityCache.store(dbKey, s, true)

        val optional = identityCache.getObject(dbKey)
        assertEquals(s, optional)
    }

    @Test
    fun basicCacheTest2(){
        val identityCache = IdentityCache(MapMaker().weakValues().makeMap(),  MapMaker().weakKeys().makeMap(), MapMaker().weakValues().makeMap())
        val s = "adeus"
        val key1 = byteArrayOf(1)
        val dbKey = PersistentKey(key1)
        identityCache.store(dbKey, s, true)

        val key2 = byteArrayOf(1)
        val optional = identityCache.getObject(PersistentKey(key2))
        assertEquals(s, optional)
    }

    @Test
    fun weakReferenceTest(){
        val identityCache = IdentityCache(MapMaker().weakValues().makeMap(), MapMaker().weakKeys().makeMap(), MapMaker().weakValues().makeMap())
        createStoreAndForget(identityCache)

        //to make sure that GC performs when requested
        createLotsOfStupidObjects()

        System.gc()
        val optional = identityCache.getObject(TestKey(1))
        assertNull(optional)
    }

    private fun createLotsOfStupidObjects() {
        val myTime = System.currentTimeMillis() + 1000
        while (System.currentTimeMillis() < myTime) {
            StupidObject(System.currentTimeMillis())
        }
    }

    private fun createStoreAndForget(identityCache: IdentityCache){
        val linkedList = LinkedList<Any>()
        identityCache.store(PersistentKey(byteArrayOf(1)), linkedList, true)
    }
}

class TestKey(private val key:Int):IRefIdentity{

    override fun getKey(): Int {
        return key
    }

    override fun toString(): String {
        return Integer.toString(key)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as TestKey

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key
    }
}

class PersistentKey(override val value: ByteArray) : IDbIdentity {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as PersistentKey

        if (!Arrays.equals(value, other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(value)
    }


}

class StupidObject(val s:Long)