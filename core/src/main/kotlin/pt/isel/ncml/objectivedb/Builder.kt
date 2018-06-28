package pt.isel.ncml.objectivedb

import pt.isel.ncml.objectivedb.configuration.ConfigBuilder
import pt.isel.ncml.objectivedb.configuration.ConfigModule
import pt.isel.ncml.objectivedb.configuration.Configuration
import pt.isel.ncml.objectivedb.configuration.IConfiguration
import pt.isel.ncml.objectivedb.serialization.IStoreFilter
import pt.isel.ncml.objectivedb.serialization.SerializationModule
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Created by nuno on 3/23/17.
 */

interface IDatabaseBuilder {
    /**
     * Builds the database with the configuration passed as parameter
     */
    fun build(conf: IConfiguration):ObjectiveDB
    fun setStorageFilter(filter: IStoreFilter): IDatabaseBuilder
    /**
     * Sets the lock strategy for the database instance
     */
    fun setLockStrategy(lockSupplier: ()->ILock): IDatabaseBuilder
    /**
     * Specifies if an existing database file should be overwritten when building the database.
     */
    fun setOverwrite(value:Boolean): IDatabaseBuilder
}

/**
 * Helper object for database creation
 */
object Builder {

    /**
     * Obtains a database builder
     */
    fun dbBuilder() : IDatabaseBuilder = DbBuilder()

    /**
     * Obtains a database configuration builder
     */
    fun configBuilder():ConfigBuilder = Configuration()

}

/**
 * Builder for new database
 * */
private class DbBuilder : IDatabaseBuilder {
    private var storeFilter: IStoreFilter = object : IStoreFilter {}
    private var lockSupplier : ()->ILock = { Lock(ReentrantReadWriteLock(true)) }
    private var overwrite: Boolean = true

    override fun build(conf: IConfiguration): ObjectiveDB {
        if(overwrite) deleteIfExist(conf.fileName)

        val objectiveComponent = DaggerIObjectiveComponent.builder()
                .configModule(ConfigModule(conf))
                .serializationModule(SerializationModule(storeFilter))
                .objectiveLockModule(ObjectiveLockModule(lockSupplier))
                .build()
        return (objectiveComponent as IObjectiveComponent).getDb()
    }

    private fun deleteIfExist(fileName: String) {
        val path = Paths.get(fileName)
        if(Files.exists(path)){
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach{
                        println("Delete file $it : ${it.delete()}")
                    }
        }
    }

    override fun setOverwrite(value: Boolean): IDatabaseBuilder {
        this.overwrite = value
        return this
    }


    override fun setStorageFilter(filter: IStoreFilter): IDatabaseBuilder {
        storeFilter = filter
        return this;
    }

    override fun setLockStrategy(lockSupplier: ()->ILock): IDatabaseBuilder {
        this.lockSupplier = lockSupplier
        return this
    }

}
