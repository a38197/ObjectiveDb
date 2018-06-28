package pt.isel.ncml.objectivedb.test

import com.google.common.base.Stopwatch
import org.junit.After
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.ncml.objectivedb.test.model.ArrayEntity
import pt.isel.ncml.objectivedb.test.model.IDatabase
import pt.isel.ncml.objectivedb.test.model.PrimitiveEntity
import pt.isel.ncml.objectivedb.test.model.ReferenceEntity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Created by nuno on 3/25/17.
 */
abstract class DbTestBase {

    abstract val database : IDatabase

    private val logger : Logger = LoggerFactory.getLogger(javaClass)
    //Junit cria uma instancia para cada teste.
    private var stopwatch by Delegates.notNull<Stopwatch>()
    private var _testName by Delegates.notNull<String>()
    private var started = false;
    private var stopped = false;
    private var unit = TimeUnit.MILLISECONDS

    fun doTimed(todo:()->Unit){
        startTimer()
        todo()
        stopTimer()
    }

    private fun startTimer() {
        _testName = getTestName()
        started = true
        stopwatch = Stopwatch.createStarted()
    }

    private fun stopTimer() {
        stopped = true;
        val elapsed = stopwatch.stop().elapsed(unit)
        logger.info("Test $_testName from class ${javaClass.simpleName} took $elapsed ${unit}")
    }

    @After
    fun assureStopped(){
        if(started && !stopped){
            stopTimer()
        }
    }

    private fun getTestName(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val stackTraceElement = stackTrace[STACK_INDEX]
        return stackTraceElement.methodName
    }

    private companion object {
        //getStackTrace;getTestName;startTimer;doTimed;testName
        const val STACK_INDEX = 4
    }
}

private val rdm = Random()

fun randomPrimitiveEntity() : PrimitiveEntity {
    return randomizePrimitiveEntity(PrimitiveEntity())
}

fun randomizePrimitiveEntity(toRandom:PrimitiveEntity) : PrimitiveEntity {
    with(toRandom){
        character = (rdm.nextInt() % 255).toChar()
        integer = rdm.nextInt()
        long = rdm.nextLong()
        double = rdm.nextDouble()
        float = rdm.nextFloat()
    }
    return toRandom
}

fun randomReferenceEntity() : ReferenceEntity {
    //boxed values are still normal objected
    val obj: Any = when (rdm.nextInt() % 3) {
        0 -> rdm.nextInt()
        1 -> rdm.nextBoolean()
        2 -> rdm.nextInt().toByte()
        else -> Any()
    }
    return ReferenceEntity(obj)
}

fun randomArrayEntity(size:Int) : ArrayEntity<Any>{
    return ArrayEntity(Array(size, { Any() }))
}

fun recursiveReference(depth: Int, ctr:(Any)->ReferenceEntity) : ReferenceEntity{
    val temp = Any()
    var curr = ctr(temp)
    val first = ctr(curr)
    for(i in 1..depth){
        val next = ctr(temp)
        curr.referant = next
        curr = next
    }
    curr.referant = first
    return first
}

fun recursiveReference(depth:Int) : ReferenceEntity {
    val ctr : (Any)->ReferenceEntity = ::ReferenceEntity
    return recursiveReference(depth, ctr)
}