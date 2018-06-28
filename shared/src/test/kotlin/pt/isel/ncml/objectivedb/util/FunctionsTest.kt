package pt.isel.ncml.objectivedb.util

import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * Created by nuno on 4/23/17.
 */
class FunctionsTest {

    @Test
    fun testSynchronized() {
        val value = AtomicInteger(0)
        var error = false
        val out = System.out

        val memorized = memorizeSync {
            //Should only enter here once
            out.println("Thread ${Thread.currentThread().name} entered lock")
            Thread.sleep(1000)
            if(value.get() != 0){
                error = true
                throw IllegalStateException("Thread ${Thread.currentThread().name} value is != 0")
            }

            value.incrementAndGet()
        }

        val t1 = thread(start = false, name = "Thread 1") {
            if( memorized() != 1 ){
                error = true
                throw IllegalStateException("Thread 1 memorized is != 1")
            }
        }

        val t2 = thread(start = false, name = "Thread 2") {
            if( memorized() != 1 ){
                error = true
                throw IllegalStateException("Thread 2 memorized is != 1")
            }
        }

        t1.start()
        t2.start()
        if(t1.isAlive)
            t1.join()
        if(t2.isAlive)
            t2.join()

        assertFalse(error)
    }


}