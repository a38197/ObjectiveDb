package pt.isel.ncml.objectivedb

import org.junit.Test

class TestDbLoad {

    @Test
    fun testLoadDb(){
        val run1 = createAndGetStrings(true)
        assert(run1.size == 1)
        val run2 = createAndGetStrings(false)
        assert(run2.size == 2)
    }

    private fun createAndGetStrings(overwrite:Boolean): Collection<String> {
        val config = Builder.configBuilder().build("TestDb.odb")
        val db = Builder.dbBuilder()
                .setOverwrite(overwrite)
                .build(config)

        db.manage("Ola")
        val allFrom = db.query().allFrom(String::class.java)
        db.close()
        return allFrom
    }
}