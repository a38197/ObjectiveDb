package pt.isel.ncml.objectivedb.index

/**
 * Created by Nuno on 04/06/2017.
 */

interface IDbIndex {
    val immutableIndex:ByteArray?
    val mutableIndex:ByteArray?
}