package pt.isel.ncml.objectivedb.test.benchmark

import com.google.common.base.Stopwatch
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by nuno on 4/12/17.
 */

private typealias Bench = () -> Unit

class BenchMaker(private val unit: TimeUnit) {
    private val testList = ArrayList<Bench>()

    @FunctionalInterface
    interface ITest {
        @Throws(Throwable::class)
        fun execute() : Unit
    }

    fun addTest(testName: String, warmup: Long, times: Long, test: ITest): BenchMaker {
        testList.add(getTest(testName, warmup, times, test))
        return this
    }

    fun benchmark() {
        testList.forEach { test ->
            try {
                test()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }

    private fun getTest(testName: String, warmup: Long, times: Long, test: ITest): Bench {
        return {
            log("Warming up test %s %d times", testName, warmup)
            for (i in 0..warmup - 1) {
                test.execute()
            }

            val stopwatch = Stopwatch.createUnstarted()
            try {
                log("Running test %s %d times", testName, times)
                stopwatch.start()
                for (i in 0..times - 1) {
                    test.execute()
                }
                val elapsed = stopwatch.elapsed(unit)
                log("Test %s runned in %d %s", testName, elapsed, unit)
                log("Average test time was %f %s", elapsed.toDouble() / times.toDouble(), unit)
            } catch (throwable: Throwable) {
                log("Test %s executed with error", throwable, testName)
            } finally {
                stopwatch.stop()
            }
        }
    }

    private fun log(message: String, vararg arguments: Any) {
        val format = String.format(message, *arguments)
        println(format)
    }

    private fun log(message: String, throwable: Throwable, vararg arguments: Any) {
        val format = String.format(message, *arguments)
        System.err.println(format)
        throwable.printStackTrace(System.err)
    }
}
