package pt.isel.ncml.objectivedb.test.model

/**
 * Created by nuno on 3/18/17.
 */

interface IQuery

interface IDatabase {
    fun read(query: IQuery):Collection<Any>
    fun save(obj: Any)
    fun delete(obj: Any)
    fun commit()
}