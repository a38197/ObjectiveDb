package pt.isel.ncml.objectivedb

import org.junit.*
import org.junit.Assert.*
import pt.isel.ncml.objectivedb.test.model.PDouble
import pt.isel.ncml.objectivedb.test.model.PInt
import pt.isel.ncml.objectivedb.test.model.ReferenceEntity
import java.util.*

/**
 * Created by Mario on 2017-04-19.
 */
class DbBuilderTest{

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
    fun testBuilderTest(){
        assertNotNull(db)
        assertTrue(db is ObjDbImplement)
        db.manage(Bananas(3, Batatas(), "a"))
    }

    @Test
    fun testQueryBuilderTest(){
        assertNotNull(db)
        assertTrue(db is ObjDbImplement)
        val bananas = Bananas(3, Batatas(), "asdcds")
        val batatas = Batatas()
        db.manage(bananas)
        db.manage(batatas)
        val result = db.query()
                .allFrom(Bananas::class.java)
                .elementAt(0)

        assertTrue(bananas == result && bananas === result)
    }

    @Test
    fun testQueryBuilderGiraTest(){
        assertNotNull(db)
        assertTrue(db is ObjDbImplement)
        val arrayOf : Array<Any> = arrayOf("string1","string2", "string3", Bananas(3, Batatas(), "asdcds"))
        val gira = Gira(arrayOf)
        db.manage(gira)
        val result = db.query()
                .allFrom(Gira::class.java)
                .elementAt(0)

        assertTrue(result == gira && gira === result)
    }

    /*
    * with class name index -> 1_000_000 : 2 min
    * */

    @Test
    fun testBasicGc() {
        prepareTest(db)
        System.gc()
        System.gc()
        val result = db.query().allFrom(GcTester::class.java)
        assertEquals(2, result.size)
        val first = result.asSequence().filter { o -> "A".equals(o.name) }.first()
        assertNull(first.obj2)
    }

    private fun prepareTest(db: ObjectiveDB){
        val a = GcTester("A", null, null)
        val b = GcTester("B", null, null)
        val c = GcTester("C", null, null)
        a.obj1 = b
        a.obj2 = c
        db.manage(a)
        a.obj2 = null
        db.manage(a)
    }

    @Test
    fun testNotBasicGc() {

        prepareNotBasicTest(db)
        System.gc()
        System.gc()
        val result = db.query().allFrom(GcTester::class.java)
        assertEquals(2, result.size)
        val first = result.asSequence().filter { o -> "A".equals(o.name) }.first()
        assertNull(first.obj2)
    }

    private fun prepareNotBasicTest(db: ObjectiveDB){
        val a = GcTester("A", null, null)
        val b = GcTester("B", null, null)
        val c = GcTester("C", null, null)
        a.obj1 = b
        a.obj2 = c
        db.manage(a)
        a.obj2 = null
        db.manage(a)
    }

    @Test
    fun testMultipleRootNotBasicGc() {


        prepareMultipleRootNotBasicTest(db)
        System.gc()
        System.gc()
        val result = db.query().allFrom(GcTester::class.java)
        assertEquals(2, result.size)
        val first = result.asSequence().filter { o -> "D".equals(o.name) }.first()
        assertTrue(first.obj1!!.name.equals("C"))
    }

    private fun prepareMultipleRootNotBasicTest(db: ObjectiveDB){
        val a = GcTester("A", null, null)
        val b = GcTester("B", null, null)
        val c = GcTester("C", null, null)
        val d = GcTester("D", null, null)
        a.obj1 = b
        b.obj1 = c
        d.obj1 = c
        db.manage(a)
        db.manage(d)
        db.unmanage(a)
    }

    @Test
    fun testMultipleRootNotBasicGc2() {


        prepareMultipleRootNotBasicTest2(db)
        System.gc()
        System.gc()
        val result = db.query().allFrom(GcTester::class.java)
        assertEquals(3, result.size)
        val first = result.asSequence().filter { o -> "D".equals(o.name) }.first()
        assertTrue(first.obj1!!.name.equals("B"))
        assertTrue(first.obj1!!.obj1!!.name.equals("C"))
    }

    private fun prepareMultipleRootNotBasicTest2(db: ObjectiveDB){
        val a = GcTester("A", null, null)
        val b = GcTester("B", null, null)
        val c = GcTester("C", null, null)
        val d = GcTester("D", null, null)
        a.obj1 = b
        b.obj1 = c
        d.obj1 = b
        db.manage(a)
        db.manage(d)
        db.unmanage(a)
    }

    @Test
    fun testMultipleRootNotBasicGc3() {

        prepareMultipleRootNotBasicTest3(db)
        System.gc()
        System.gc()
        val result = db.query().allFrom(GcTester::class.java)
        assertEquals(3, result.size)
        val first = result.asSequence().filter { o -> "A".equals(o.name) }.first()
        assertTrue(first.obj1!!.name.equals("B"))
        assertTrue(first.obj1!!.obj1!!.name.equals("C"))
    }

    private fun prepareMultipleRootNotBasicTest3(db: ObjectiveDB){
        val a = GcTester("A", null, null)
        val b = GcTester("B", null, null)
        val c = GcTester("C", null, null)
        val d = GcTester("D", null, null)
        a.obj1 = b
        b.obj1 = c
        d.obj1 = b
        db.manage(a)
        db.manage(d)
        db.unmanage(d)
    }
    @Test
    fun testBasicGcWithUnamanage() {
        prepareTestUnmanage(db)
        System.gc()
        System.gc()
        val result = db.query().allFrom(GcTester::class.java)
        assertEquals(2, result.size)
        val first = result.asSequence().filter { o -> "C".equals(o.name) }.first()
        assertEquals("B", first.obj1!!.name)
        assertTrue(first is GcTester)
    }

    private fun prepareTestUnmanage(db: ObjectiveDB){
        val a = GcTester("A", null, null)
        val b = GcTester("B", null, null)
        val c = GcTester("C", null, null)
        a.obj1 = b
        c.obj1 = b
        db.manage(a)
        db.manage(c)
        db.unmanage(a)
    }

    @Ignore
    @Test
    fun letTryStackOVerFlowTest(){
        //100_000 -> 13s -> add gc -> 17s
        //200_000 -> 27s -> add gc -> 35s
        //400_000 -> 58s -> add gc -> 1:09s
        //800_000 -> 2:00 -> add gc -> infinite store
        val clazz = storeList(db, 1_000)
        System.gc()
        System.gc()
        System.gc()
        System.out.println("begin queryIndex")
        val end = db.query().allFrom(clazz)
        assert(end.size == 1)
        System.out.println("end queryIndex")
        val result : LinkedList<String> = end.iterator().next()
        assertTrue(result.isNotEmpty())
        var i:Int = 0;
        for (s:String in result) {
            i++;
            System.out.println(s)
            assertEquals(Integer.toString(i), s)
        }
    }

    @Test
    fun severalClassesTest() {

        val list = ArrayList<Any>()
        list.add(PInt(3))
        list.add(PDouble(3.5))

        db.manage(list)
        db.manage(PInt(50))

        val ints = db.query().allFrom(PInt::class.java)
        val doubles = db.query().allFrom(PDouble::class.java)
        val lists = db.query().allFrom(ArrayList::class.java)

        assertEquals(2, ints.size)
        assertEquals(1, doubles.size)
        assertEquals(1, lists.size)
        assertTrue(lists.elementAt(0).contains(ints.first()) || lists.elementAt(0).contains(ints.elementAt(1)))
        assertEquals(doubles.first(), lists.first().elementAt(1))
    }

    @Test
    fun nullReference() {

        var r: ReferenceEntity? = ReferenceEntity(null)
        db.manage(r!!)
        r = null
        System.gc()
        System.gc()
        System.gc()
        System.gc()

        val elementAt = db.query().allFrom(ReferenceEntity::class.java).elementAt(0)
        assertNotNull(elementAt)
        assertNull(elementAt.referant)
    }

    @Test
    fun referenceDelete() {

        var i: PInt? = PInt(50)
        var r2: ReferenceEntity? = ReferenceEntity(i)
        db.manage(r2!!)
        db.unmanage(r2!!)
        i = null
        r2 = null
        System.gc()
        System.gc()
        System.gc()
        System.gc()

        r2 = db.query().allFrom(ReferenceEntity::class.java).elementAtOrNull(0)
        assertNull(r2)
    }

    @Ignore
    @Test
    fun letTryStackOVerFlowTest2(){

        //100_000 -> 13s -> add gc -> 17s
        //200_000 -> 27s -> add gc -> 35s
        //400_000 -> 58s -> add gc -> 1:09s
        //800_000 -> 2:00 -> add gc -> infinite store
        val start = System.currentTimeMillis()
        println("Start")
        storeList2(db, 1_000)
        System.gc()
        System.gc()
        System.gc()
        System.out.println("begin queryIndex")
        val end = db.query().allFrom(GcTester::class.java)
        assert(end.size == 1_001, {"Got ${end.size}"})
        System.out.println("end queryIndex")
        var result : GcTester = end.asSequence().filter { o -> "0" == o.name }.first()
        Assert.assertTrue(result.obj1 != null)
        var i:Int = 0;
        while(result.obj1!= null){
            System.out.println(result.name)
            Assert.assertEquals(Integer.toString(i), result.name)
            result = result.obj1!!
            i++;
        }
        println("Start at ${System.currentTimeMillis() - start}")
    }


    private fun storeList2(db: ObjectiveDB, size: Int) {
        var gcTester = GcTester("0",null, null)
        val first = gcTester
        for (i in 1..size) {
            val cenas = GcTester(i.toString(), null, null)
            gcTester.obj1 = cenas
            gcTester = cenas
        }
        System.out.println("begin store")
        db.manage(first)
        System.out.println("end store")

    }

    private fun storeList(db: ObjectiveDB, size: Int): Class<LinkedList<String>> {
        val list = LinkedList<String>()
        for (i in 1..size) {
            list.add(i.toString())
        }
        System.out.println("begin store")
        db.manage(list)
        System.out.println("end store")
        return list.javaClass
    }

}

class Gira() {

    constructor(cenas2 : Array<Any>):this(){
        cenas = cenas2
    }

    var cenas :Array<Any>? = null

    override fun hashCode(): Int {
        return Arrays.hashCode(cenas)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Gira

        if (!Arrays.equals(cenas, other.cenas)) return false

        return true
    }
}

class Bananas(){

    constructor(b:Int, b2: Batatas, b3:String):this(){
        banana = b
        batatas2 = b2
        batatas = b3
    }

    var banana:Int = 0;
    var batatas2:Batatas? = null
    var batatas:String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Bananas

        if (banana != other.banana) return false
        if (batatas2 != other.batatas2) return false
        if (batatas != other.batatas) return false

        return true
    }

    override fun hashCode(): Int {
        var result = banana
        result = 31 * result + (batatas2?.hashCode() ?: 0)
        result = 31 * result + (batatas?.hashCode() ?: 0)
        return result
    }
}

class Batatas(){

    val batatinhas = 9
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Batatas

        if (batatinhas != other.batatinhas) return false

        return true
    }

    override fun hashCode(): Int {
        return batatinhas
    }
}

data class GcTester(val name:String, var obj1:GcTester?,var obj2:GcTester?)
