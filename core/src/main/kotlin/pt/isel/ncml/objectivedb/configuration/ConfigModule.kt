package pt.isel.ncml.objectivedb.configuration

import dagger.Module
import dagger.Provides
import pt.isel.ncml.objectivedb.index.ConfigIndexes
import pt.isel.ncml.objectivedb.storage.DbFileName

/**
 * Created by Nuno on 22/05/2017.
 */

@Module
class ConfigModule(
        private val config: IConfiguration
) {

    @Provides
    fun getIndexes(): ConfigIndexes {
        return config.indexes
    }

    @Provides
    @DbFileName
    fun providesFileName():String{
        return config.fileName
    }

    @Provides
    fun providesConfiguration(): IConfiguration {
        return config
    }
}