package pt.isel.ncml.objectivedb.query

import org.junit.Assert.assertTrue
import org.junit.Test
import pt.isel.ncml.objectivedb.test.model.AllPrimitives

/**
 * Created by nuno on 5/18/17.
 */
class UserQueryImplTest {

    private val dbInterface = MockDbInterface()
    private val module = QueryModule()

    @Test
    fun simpleAnd() {
        module.factory(dbInterface).query()
                .from(AllPrimitives::class.java)
                .where { false }
                .and()
                .where { true }
                .end()
                .query()

        assertTrue(AndQExpr::class.java.isInstance(dbInterface.expr))
        val andQExpr = dbInterface.expr as AndQExpr
        assertTrue(MemoryExpr::class.java.isInstance(andQExpr.left))
        assertTrue(MemoryExpr::class.java.isInstance(andQExpr.right))
    }

    @Test
    fun andOrChain() {
        module.factory(dbInterface).query()
                .from(AllPrimitives::class.java)
                .where { false }
                .and()
                .where { true }
                .or()
                .where { true }
                .end()
                .query()

        assertTrue(OrQExpr::class.java.isInstance(dbInterface.expr))
        val orQExpr = dbInterface.expr as OrQExpr
        assertTrue(AndQExpr::class.java.isInstance(orQExpr.left))
        assertTrue(MemoryExpr::class.java.isInstance(orQExpr.right))
        val andQExpr = orQExpr.left as AndQExpr
        assertTrue(MemoryExpr::class.java.isInstance(andQExpr.left))
        assertTrue(MemoryExpr::class.java.isInstance(andQExpr.right))
    }

    @Test
    fun single() {
        module.factory(dbInterface).query()
                .from(AllPrimitives::class.java)
                .where { false }
                .end()
                .query()

        assertTrue(SingleQExprImpl::class.java.isInstance(dbInterface.expr))
    }

    @Test
    fun inner() {
        module.factory(dbInterface).query()
                .from(AllPrimitives::class.java)
                .where { false }
                .and()
                    .inner()
                    .where { false }
                    .or()
                    .where { true }
                    .end()
                .end()
                .query()

        assertTrue(AndQExpr::class.java.isInstance(dbInterface.expr))
        val andQExpr = dbInterface.expr as AndQExpr
        assertTrue(MemoryExpr::class.java.isInstance(andQExpr.left))
        assertTrue(InnerQExpr::class.java.isInstance(andQExpr.right))
        val innerQExpr = andQExpr.right as InnerQExpr
        assertTrue(OrQExpr::class.java.isInstance(innerQExpr.inner))
        val orQExpr = innerQExpr.inner as OrQExpr
        assertTrue(MemoryExpr::class.java.isInstance(orQExpr.left))
        assertTrue(MemoryExpr::class.java.isInstance(orQExpr.right))
    }
}