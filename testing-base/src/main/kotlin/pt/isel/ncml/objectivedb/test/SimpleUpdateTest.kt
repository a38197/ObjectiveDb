package pt.isel.ncml.objectivedb.test

import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.ModelEntity
import pt.isel.ncml.objectivedb.test.model.PrimitiveEntity
import pt.isel.ncml.objectivedb.test.model.ReferenceWithPrimitive

/**
 * Created by nuno on 3/26/17.
 */

abstract class SimpleUpdateTest : DbTestBase() {

    @Test
    fun update1000PrimitiveObject(){
        val list = ArrayList<ModelEntity>(1000)
        for(i in 1..1000){
            val entity = randomPrimitiveEntity()
            database.save(entity)
            list.add(entity)
            randomizePrimitiveEntity(entity)
        }

        doTimed {
            list.forEach{
                database.save(it)
            }
        }
    }

    @Test
    fun updateObjectGraph(){
        val graph = recursiveReference(1000, { any ->
            val referenceWithPrimitive = ReferenceWithPrimitive(any, randomPrimitiveEntity())
            database.save(referenceWithPrimitive)
            randomizePrimitiveEntity(referenceWithPrimitive.primitive)
            return@recursiveReference referenceWithPrimitive
        })

        doTimed {
            database.save(graph)
        }
    }

    @Test
    fun updateAnonymousClass(){
        val anonymous = object : PrimitiveEntity() {
            val prime = 7
            var bool = false
            var reference = randomPrimitiveEntity()
        }

        database.save(anonymous)
        anonymous.bool = true
        anonymous.reference = randomPrimitiveEntity()
        randomizePrimitiveEntity(anonymous)

        doTimed {
            database.save(anonymous)
        }
    }
}