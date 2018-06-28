package pt.isel.ncml.objectivedb.query

import dagger.Module
import dagger.Provides

/**
 * Created by nuno on 5/11/17.
 */

@Module
class QueryModule {

    @Provides
    fun factory(dbInterface: DbQueryInterface): QueryFactory = Factory(dbInterface)

}

interface QueryFactory {
    fun query() : UserQuery
}

class Factory(private val dbInterface: DbQueryInterface) : QueryFactory {
    override fun query() : UserQuery = UserQueryImpl(dbInterface)
}