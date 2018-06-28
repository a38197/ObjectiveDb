package pt.isel.ncml.objectivedb.index

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder

/**
 * Created by Nuno on 02/06/2017.
 */


data class IndexDefinition(val fieldName:String, val mutable:Boolean)

/**
 * Multimap with key as ClassName and as values FieldName
 */
class ConfigIndexes : Multimap<String, IndexDefinition> by MultimapBuilder.hashKeys().hashSetValues().build<String,IndexDefinition>()