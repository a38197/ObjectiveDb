package pt.isel.ncml.objectivedb.reference

/**
 * Created by Nuno on 01/06/2017.
 */

interface IReferenceHandler {
    /**
     * States the reference size in bytes that this resolver operates
     * @return the number of bytes needed to translate a reference
     */
    val referenceSize : Int
}