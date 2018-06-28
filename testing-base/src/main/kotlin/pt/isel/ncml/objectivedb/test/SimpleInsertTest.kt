package pt.isel.ncml.objectivedb.test

import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.ModelEntity
import pt.isel.ncml.objectivedb.test.model.PrimitiveEntity
import pt.isel.ncml.objectivedb.test.model.ReferenceEntity
import java.util.*

/**
 * Created by nuno on 3/25/17.
 */

abstract class SimpleInsertTest : DbTestBase() {

    @Test
    fun insert1000SameClassItems(){
        val list = ArrayList<PrimitiveEntity>(1000)
        for(i in 1..1000){
            list.add(randomPrimitiveEntity())
        }
        doTimed {
            for(entity in list)
                database.save(entity)
        }
    }

    @Test
    fun insert1000DifferentClassItems(){
        val list = ArrayList<ModelEntity>(1000)
        for(i in 1..1000){
            when(i % 4){
                0 -> list.add(randomPrimitiveEntity())
                2 -> list.add(randomReferenceEntity())
                3 -> list.add(randomArrayEntity(5))
            }
        }
        doTimed {
            for(entity in list)
                database.save(entity)
        }
    }

    @Test
    fun insertRecursiveObjectGraph(){
        val first = recursiveReference(1000)

        doTimed {
            database.save(first)
        }
    }

    @Test
    fun insertAnonymousClass(){
        val anonymous = object : ModelEntity {
            val prime = 5
            val bool = false
            val reference = randomPrimitiveEntity()
        }

        doTimed {
            database.save(anonymous)
        }
    }

    @Test
    fun insertAlreadyReferencedObject(){
        val stored = randomPrimitiveEntity()
        database.save(stored)
        val reference = ReferenceEntity(stored)

        doTimed {
            database.save(reference)
        }
    }


}
