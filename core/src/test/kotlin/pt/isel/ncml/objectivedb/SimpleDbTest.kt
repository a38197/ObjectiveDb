package pt.isel.ncml.objectivedb

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Nuno on 18/06/2017.
 */

class SimpleDbTest {

    private lateinit var db : ObjectiveDB

    @Before
    fun setUp() {
        val config = Builder.configBuilder().build("TestDb.odb")
        db = Builder.dbBuilder()
                .setOverwrite(true)
                .build(config)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun storeAndGet() {
        val string = "some data"
        db.manage(string)

        val collection = db.query().allFrom(string.javaClass)
        assert(!collection.isEmpty())
        val first = collection.first()
        assert(string == first)
        assert(string === first)
    }

    @Test
    fun listStore() {
        db.manage(listOf("cenas"))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query().allFrom(String::class.java)
        assert(!collection.isEmpty())
        val first = collection.first()
        assert("cenas" == first)
    }

    @Test
    fun listOfTestData() {
        db.manage(linkedSetOf(ForTest(1, "one"), ForTest(2, "two")))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query().allFrom(ForTest::class.java)
        assert(collection.size == 2, {collection.size})
    }

    @Test
    fun arrayTest() {
        db.manage(arrayOf(ForTest(1, "one"), ForTest(2, "two")))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query().allFrom(ForTest::class.java)
        assert(collection.size == 2, {collection.size})
    }

    @Test
    fun testData() {
        db.manage(ForTest(1, "one"))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query().allFrom(ForTest::class.java)
        assert(collection.size == 1, {collection.size})
        assert(null !== collection.first().s)// NOSONAR
    }
}

private data class ForTest(val i: Int, val s: String)