package pt.isel.ncml.objectivedb

import org.junit.Test
import pt.isel.ncml.objectivedb.index.ConfigIndexes
import pt.isel.ncml.objectivedb.index.IndexDefinition
import pt.isel.ncml.objectivedb.query.AndQExprImp
import pt.isel.ncml.objectivedb.query.ExprImpl
import pt.isel.ncml.objectivedb.test.model.AllPrimitives
import pt.isel.ncml.objectivedb.util.ByteConverter
import java.util.*

/**
 * Created by Nuno on 24/06/2017.
 */
class OperExprAnalyzerTest{

    @Test
    fun persistentIndex() {
        val configIndexes = ConfigIndexes()
        configIndexes.put(AllPrimitives::class.java.name, IndexDefinition("f", false))
        val analyzer = OperExprAnalyzer(AllPrimitives::class.java, configIndexes)

        analyzer.visit(ExprImpl.Equals(123F, "f"))

        val immutableIndexes = analyzer.immutableIndexes

        val expected = ByteConverter.fromObject(AllPrimitives::class.java) + ByteConverter.fromFloat(123F)
        assert(Arrays.equals(expected, immutableIndexes))

    }

    @Test
    fun innerExprTest() {
        val configIndexes = ConfigIndexes()
        configIndexes.put(AllPrimitives::class.java.name, IndexDefinition("f", false))
        val analyzer = OperExprAnalyzer(AllPrimitives::class.java, configIndexes)

        val expr = AndQExprImp(
                ExprImpl.Equals(123F, "f"),
                ExprImpl.Equals(2, "s")
        )

        analyzer.visit(expr)

        val immutableIndexes = analyzer.immutableIndexes

        val expected = ByteConverter.fromObject(AllPrimitives::class.java)
        assert(Arrays.equals(expected, immutableIndexes))


    }
}