package pt.isel.ncml.objectivedb.configuration

import pt.isel.ncml.objectivedb.index.ConfigIndexes
import pt.isel.ncml.objectivedb.index.IndexDefinition

/*
Properties file may be changed between application instances while this configurations should not change
 */

/**
 * Database configuration
 */
interface IConfiguration{
    /**
     * The configured indexes
     */
    val indexes: ConfigIndexes
    /**
     * The database file name
     */
    val fileName: String
}

/**
 * Database configuration builder
 */
interface ConfigBuilder {
    fun putMutableIndex(className:String, fieldName:String) : ConfigBuilder
    fun putImmutableIndex(className:String, fieldName:String) : ConfigBuilder
    fun build(fileName:String):IConfiguration
}

internal class Configuration : IConfiguration, ConfigBuilder {

    private val _indexes = ConfigIndexes()
    override val indexes: ConfigIndexes
        get() = _indexes

    private lateinit var _fileName:String
    override val fileName: String
        get() = _fileName

    override fun putMutableIndex(className: String, fieldName: String) : ConfigBuilder {
        _indexes[className].add(IndexDefinition(fieldName, true))
        return this
    }

    override fun putImmutableIndex(className: String, fieldName: String) : ConfigBuilder {
        _indexes[className].add(IndexDefinition(fieldName, false))
        return this
    }

    override fun build(fileName: String): IConfiguration {
        _fileName = fileName
        return this
    }

}
