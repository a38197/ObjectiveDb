package pt.isel.ncml.objectivedb.test

import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.ModelEntity
import pt.isel.ncml.objectivedb.test.model.PrimitiveEntity
import pt.isel.ncml.objectivedb.test.model.ReferenceWithPrimitive

/**
 * Created by nuno on 3/26/17.
 */

val initialCapacity = 1000
abstract class SimpleDeleteTest : DbTestBase() {

    @Test
    fun delete1000PrimitiveObject(){
        val list = ArrayList<ModelEntity>(initialCapacity)
        for(i in 1..initialCapacity){
            val entity = randomPrimitiveEntity()
            database.save(entity)
            list.add(entity)
            randomizePrimitiveEntity(entity)
        }

        doTimed {
            list.forEach{
                database.delete(it)
            }
        }
    }

    @Test
    fun deleteObjectGraphReferences(){
        val graph = recursiveReference(1000, { any ->
            val referenceWithPrimitive = ReferenceWithPrimitive(any, randomPrimitiveEntity())
            database.save(referenceWithPrimitive)
            randomizePrimitiveEntity(referenceWithPrimitive.primitive)
            return@recursiveReference referenceWithPrimitive
        })

        val toDelete = ArrayList<ReferenceWithPrimitive>()
        var referant = graph.referant
        var i = 0
        while(referant != graph){
            referant as ReferenceWithPrimitive
            if(i++ % 3 == 0){
                toDelete.add(referant)
            }
            referant = referant.referant
        }

        doTimed {
            toDelete.forEach{
                database.delete(it)
            }
        }
    }

    @Test
    fun deleteAnonymousClass(){
        val anonymous = object : PrimitiveEntity() {
            val prime = 5
            var bool = false
            var reference = randomPrimitiveEntity()
        }

        database.save(anonymous)

        doTimed {
            database.delete(anonymous)
        }
    }
}