package pt.isel.ncml.objectivedb

import org.junit.After
import org.junit.Before
import org.junit.Test
import pt.isel.ncml.objectivedb.configuration.IConfiguration
import pt.isel.ncml.objectivedb.query.ExprImpl
import pt.isel.ncml.objectivedb.test.model.AllPrimitives

/**
 * Created by Nuno on 18/06/2017.
 */
class QueryAnalyzerTest {

    private lateinit var db : ObjectiveDB

    @Before
    fun setUp() {
        val config = Builder.configBuilder()
                .build("TestDb.odb")

        setUp(config)
    }

    fun setUp(config: IConfiguration) {
        db = Builder.dbBuilder()
                .setOverwrite(true)
                .build(config)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun simpleMemoryQuery() {
        db.manage(TestData(1,2))
        db.manage(TestData(3,4))
        db.manage(TestData(5,6))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query()
                .from(TestData::class.java)
                .where {
                    it.pubI == 4
                }
                .end().query()
        assert(collection.size == 1, {collection.size})
    }

    @Test
    fun simpleReflectionQuery() {
        db.manage(TestData(1,2))
        db.manage(TestData(3,4))
        db.manage(TestData(5,6))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query()
                .from(TestData::class.java)
                .where(ExprImpl.Equals(5, "privI"))
                .end().query()
        assert(collection.size == 1, {collection.size})
        val first = collection.first()
        assert(first == TestData(5,6))
        assert(first !== TestData(5,6))
    }

    @Test
    fun simpleAnd() {
        db.manage(TestData(1,4))
        db.manage(TestData(1,8))
        db.manage(TestData(2,8))

        val collection = db.query()
                .from(TestData::class.java)
                .where{ it.pubI != 4 }
                .and()
                .where(ExprImpl.Equals(1, "privI"))
                .end()
                .query()
        assert(collection.size == 1, {collection.size})
        assert(collection.first() == TestData(1,8))
    }

    @Test
    fun simpleOr() {
        db.manage(TestData(1,4))
        db.manage(TestData(1,8))
        db.manage(TestData(2,8))

        val collection = db.query()
                .from(TestData::class.java)
                .where{ it.pubI == 4 }
                .or()
                .where(ExprImpl.Equals(2, "privI"))
                .end().query()
        assert(collection.size == 2, {collection.size})
    }

    @Test
    fun innerQuery() {
        db.manage(TestData(1,1))
        db.manage(TestData(1,2))
        db.manage(TestData(1,3))

        val collection = db.query()
                .from(TestData::class.java)
                .where(ExprImpl.Equals(1, "privI"))
                .or()
                .inner()//needs to be evaluated first
                    .where { it.pubI == 2 }
                    .and()
                    .where { it.pubI % 2 == 0 }
                    .end()
                .end().query()
        assert(collection.size == 3, { collection.size })
        assert(collection.containsAll(listOf(TestData(1,1),TestData(1,2), TestData(1,3))))
    }

    @Test
    fun comparable() {
        db.manage(TestData(1,2))
        db.manage(TestData(2,3))
        db.manage(TestData(3,4))
        db.manage(TestData(5,6))
        db.manage(TestData(7,8))

        val collection = db.query()
                .from(TestData::class.java)
                .where(ExprImpl.Gte(2, "privI"))
                .and()
                .where(ExprImpl.Lt(6, "pubI"))
                .end().query()

        assert(collection.size == 2, { collection.size })
        assert(collection.containsAll(listOf(TestData(2,3),TestData(3,4))))
    }

    @Test
    fun aBitMoreComplexTest() {
        db.manage(listOf("one", "two", "three"))

        System.gc()
        System.gc()
        System.gc()

        val collection = db.query()
                .from(String::class.java)
                .where{ it.contains('e', true) }
                .end().query()

        assert(collection.size == 2, { collection.size })
        assert(collection.containsAll(listOf("one","three")))

        val collection2 = db.query().allFrom(String::class.java)
        assert(collection2.size == 3, { collection2.size })
    }

    @Test
    fun persistentIndex() {
        db.close()
        setUp(
                Builder.configBuilder().putImmutableIndex(AllPrimitives::class.java.name, "f").build("TestDb.odb")
        )

        db.manage(AllPrimitives(f = 578F))
        db.manage(AllPrimitives(f = 124F))
        db.manage(AllPrimitives(f = 365F))

        val data = db.query().from(AllPrimitives::class.java).where(ExprImpl.Equals(365F, "f")).end().query()
        assert(data.size == 1)

    }
}

private data class TestData(private val privI:Int, val pubI: Int)