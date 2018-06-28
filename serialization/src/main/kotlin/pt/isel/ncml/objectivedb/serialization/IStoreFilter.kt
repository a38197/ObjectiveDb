package pt.isel.ncml.objectivedb.serialization

/**
 * Created by Mario on 2017-04-26.
 */
@FunctionalInterface
interface IStoreFilter {
    fun canBeStored(o:Any) : Boolean{
        return true
    }
}