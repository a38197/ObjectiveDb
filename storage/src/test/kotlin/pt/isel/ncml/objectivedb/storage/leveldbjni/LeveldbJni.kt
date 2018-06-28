package pt.isel.ncml.objectivedb.storage.leveldbjni

import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Created by Nuno on 20/05/2017.
 */

class LeveldbJniTest {

    @Test
    fun putAndGet() {
        val tempDir = System.getProperty("java.io.tmpdir")
        val file = File(tempDir, "db.ldb")
        val options = Options()
        options.createIfMissing()
        val db: DB = JniDBFactory.factory.open(file, options)
        val key = "key"
        val value = "value"
        db.put(key.toByteArray(), value.toByteArray())

        val v = db.get(key.toByteArray())
        assertEquals(value, String(v))
    }

    @Test
    fun testBatch() {
        val tempDir = System.getProperty("java.io.tmpdir")
        val file = File(tempDir, "db2.ldb")
        val options = Options()
        options.createIfMissing()
        val db: DB = JniDBFactory.factory.open(file, options)
        val key1 = "key1"
        val value1 = "value1"
        val batch = db.createWriteBatch()
        batch.put(key1.toByteArray(), value1.toByteArray())

        val key2 = "key2"
        val value2 = "value2"
        batch.put(key2.toByteArray(), value2.toByteArray())

        val key3 = "key3"
        val value3 = "value3"
        batch.put(key3.toByteArray(), value3.toByteArray())
        db.write(batch)
        batch.close()

        val v1 = db.get(key1.toByteArray())
        assertEquals(value1, String(v1))
        val v2 = db.get(key2.toByteArray())
        assertEquals(value2, String(v2))
        val v3 = db.get(key3.toByteArray())
        assertEquals(value3, String(v3))
    }


}