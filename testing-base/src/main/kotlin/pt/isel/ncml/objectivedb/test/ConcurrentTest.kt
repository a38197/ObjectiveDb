package pt.isel.ncml.objectivedb.test

import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.PrimitiveEntity
import java.util.*

/**
 * Created by Mario on 2017-03-27.
 */
abstract class ConcurrentTest() : DbTestBase(){


    @Test
    fun multithreadedTest(){
        val list = ArrayList<PrimitiveEntity>(1000)
        for (i in 1..1000) {
            list.add(randomPrimitiveEntity())
        }
        val list2 = ArrayList<PrimitiveEntity>(1000)
        for (i in 1..1000) {
            list2.add(randomPrimitiveEntity())
        }

        val t1 = Thread({ -> generatePrimitive(list) })
        val t2 = Thread({ -> generatePrimitive(list2) })

        doTimed {
            t1.start()
            t2.start()
            t1.join()
            t2.join()
        }

    }

    private  fun generatePrimitive(list : List<Any>) {
            for (entity in list)
                database.save(entity)
    }

}