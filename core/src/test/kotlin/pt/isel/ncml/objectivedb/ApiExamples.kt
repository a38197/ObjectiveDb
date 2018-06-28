package pt.isel.ncml.objectivedb

import pt.isel.ncml.objectivedb.query.ExprImpl
import pt.isel.ncml.objectivedb.serialization.IStoreFilter

/**
 * Created by Nuno on 09/07/2017.
 */

class ApiExamples {

    fun createDatabaseInstance() {
        val customConfig = Builder
                .configBuilder()
                .putImmutableIndex(TestDto::class.java.name, "numberField")
                .putMutableIndex(TestDto::class.java.name, "booleanField")
                .build("DbFileName.odb")

        val objectiveDB: ObjectiveDB = Builder.dbBuilder()
                .setStorageFilter(CustomStoreFilter())
                .setLockStrategy(::CustomLock)
                .build(customConfig)
    }

    private lateinit var db : ObjectiveDB

    fun objectManage() {
        data class Node(val name:String, val index:Int, val next:Node?)
        val first = Node("first", 0, Node("second", 1, null))
        db.manage(first)
    }

    fun objectUnmanage() {
        data class Node(val name:String, val index:Int, val next:Node?)
        val first = Node("first", 0, Node("second", 1, null))
        val third = Node("third", 2, first)
        db.manage(first) //OP1
        db.manage(third) //OP2
        db.unmanage(first) //OP3
        db.unmanage(third) //OP4
    }

    fun predicateQuery(){
        val data: Collection<TestDto> = db
                .query()
                    .from(TestDto::class.java)
                        .where { it.numberField > 3 }
                        .or()
                        .inner()
                            .where { it.numberField < 3 }
                            .and()
                            .where { it.booleanField == true }
                        .end()
                    .end()
                .query()
    }

    fun predicateQuery2() {
        db.query().from(TestDto::class.java).where{ dto -> dto.booleanField == true }.end().query()
        db.query().from(TestDto::class.java).where(ExprImpl.Equals(true, "booleanField")).end().query()
    }

    fun metadataQuery(){
        val data: Collection<TestDto> = db
                .query()
                    .from(TestDto::class.java)
                        .where(ExprImpl.Gt(3, "numberField"))
                        .or()
                        .inner()
                            .where(ExprImpl.Lt(3, "numberField"))
                            .and()
                            .where(ExprImpl.Constant(true, "booleanField"))
                        .end()
                    .end()
                .query()
    }
}

private data class TestDto(val numberField:Int, val booleanField:Boolean)
private class CustomStoreFilter : IStoreFilter {

}
private class CustomLock : ILock {
    override fun finishWrite() {
        TODO("not implemented")
    }

    override fun beginRead() {
        TODO("not implemented")
    }

    override fun finishRead() {
        TODO("not implemented")
    }

    override fun beginWrite() {
        TODO("not implemented")
    }

}