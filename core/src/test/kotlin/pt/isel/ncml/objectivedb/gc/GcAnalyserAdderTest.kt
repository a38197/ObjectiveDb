package pt.isel.ncml.objectivedb.gc

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.isel.ncml.objectivedb.IDbIdentity
import pt.isel.ncml.objectivedb.serialization.GcData

/**
 * Created by Mario on 2017-06-02.
 */
class GcAnalyserAdderTest{

    @Test
    fun testSimpleGraph(){
        val data = mutableMapOf<IDbIdentity, GcData>()
        val gcAnalyserAdder = GcAnalyserAdder(data, mutableSetOf())
        val root = DbKey("A".toByteArray())
        val analyserAdder = gcAnalyserAdder.newRoot(root)
        val refB = DbKey("B".toByteArray())
        analyserAdder.addReference(refB)
        val refC = DbKey("C".toByteArray())
        analyserAdder.addReference(refC)

        assertEquals(3, data.size)

        val rootGcData = data.get(root)!!
        assertEquals(0, rootGcData.isReferencedFrom.size)
        assertEquals(2, rootGcData.references.size)

        val bGcData = data.get(refB)!!
        assertEquals(1, bGcData.isReferencedFrom.size)
        assertEquals(root, bGcData.isReferencedFrom.elementAt(0))

        val cGcData = data.get(refC)!!
        assertEquals(1, cGcData.isReferencedFrom.size)
        assertEquals(root, cGcData.isReferencedFrom.elementAt(0))
    }

    @Test
    fun testMultiRoot(){
        val data = mutableMapOf<IDbIdentity, GcData>()
        val gcAnalyserAdder = GcAnalyserAdder(data, mutableSetOf())
        val rootA = DbKey("A".toByteArray())
        val analyserRootA = gcAnalyserAdder.newRoot(rootA)
        val refB = DbKey("B".toByteArray())
        analyserRootA.addReference(refB)
        val rootC = DbKey("C".toByteArray())
        val analyserRootC = analyserRootA.newRoot(rootC)
        val refD = DbKey("D".toByteArray())
        analyserRootC.addReference(refD)
        val refE = DbKey("E".toByteArray())
        analyserRootC.addReference(refE)

        assertEquals(5, data.size)

        val rootGcData = data.get(rootA)!!
        assertEquals(0, rootGcData.isReferencedFrom.size)
        assertEquals(4, rootGcData.references.size)

        val bGcData = data.get(refB)!!
        assertEquals(1, bGcData.isReferencedFrom.size)
        assertEquals(rootA, bGcData.isReferencedFrom.elementAt(0))

        val cGcData = data.get(rootC)!!
        assertEquals(1, cGcData.isReferencedFrom.size)
        assertEquals(rootA, cGcData.isReferencedFrom.elementAt(0))
        assertEquals(2, cGcData.references.size)

        val dGcData = data.get(refD)!!
        assertEquals(2, dGcData.isReferencedFrom.size)
        assertEquals(rootA, dGcData.isReferencedFrom.elementAt(0))
        assertEquals(rootC, dGcData.isReferencedFrom.elementAt(1))

        val eGcData = data.get(refE)!!
        assertEquals(2, eGcData.isReferencedFrom.size)
        assertEquals(rootA, eGcData.isReferencedFrom.elementAt(0))
        assertEquals(rootC, eGcData.isReferencedFrom.elementAt(1))

    }

    @Test
    fun testMultiRootCircular(){
        val data = mutableMapOf<IDbIdentity, GcData>()
        val gcAnalyserAdder = GcAnalyserAdder(data, mutableSetOf())
        val rootA = DbKey("A".toByteArray())
        val analyserRootA = gcAnalyserAdder.newRoot(rootA)
        val refB = DbKey("B".toByteArray())
        analyserRootA.addReference(refB)
        val rootC = DbKey("C".toByteArray())
        val analyserRootC = analyserRootA.newRoot(rootC)
        val refD = DbKey("D".toByteArray())
        analyserRootC.addReference(refD)
        val refE = DbKey("E".toByteArray())
        analyserRootC.addReference(refE)
        analyserRootC.addReference(rootA)

        assertEquals(5, data.size)

        val rootGcData = data.get(rootA)!!
        assertEquals(1, rootGcData.isReferencedFrom.size)
        assertEquals(4, rootGcData.references.size)

        val bGcData = data.get(refB)!!
        assertEquals(2, bGcData.isReferencedFrom.size)
        assertEquals(rootA, bGcData.isReferencedFrom.elementAt(0))

        val cGcData = data.get(rootC)!!
        assertEquals(1, cGcData.isReferencedFrom.size)
        assertEquals(rootA, cGcData.isReferencedFrom.elementAt(0))
        assertEquals(3, cGcData.references.size)

        val dGcData = data.get(refD)!!
        assertEquals(2, dGcData.isReferencedFrom.size)
        assertEquals(rootA, dGcData.isReferencedFrom.elementAt(0))
        assertEquals(rootC, dGcData.isReferencedFrom.elementAt(1))

        val eGcData = data.get(refE)!!
        assertEquals(2, eGcData.isReferencedFrom.size)
        assertEquals(rootA, eGcData.isReferencedFrom.elementAt(0))
        assertEquals(rootC, eGcData.isReferencedFrom.elementAt(1))

    }
}

class DbKey(override val value: ByteArray):IDbIdentity{

}