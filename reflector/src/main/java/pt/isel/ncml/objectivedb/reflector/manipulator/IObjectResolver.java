package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reference.IReferenceHandler;

/**
 * This interface represents the resolve method for the object references.
 * The returned byte array should represent an object identifier that can be obtained later.
 * Remember that an object entry on the database must contain a flat representation:
 * <ul>
 *     <li>Headers</li>
 *     <li>Primitive fields</li>
 *     <li>Serialized object references (pointers to another objects on the same store)</li>
 * </ul>
 */
public interface IObjectResolver extends IReferenceHandler {
    /**
     * Serializes and object <strong>reference</strong> to a byte array
     * @param obj the object to extract the reference
     * @return a serialized object reference
     */
    byte[] toByteArray(Object obj);

    /**
     * Serializes and object <strong>reference</strong> to a byte[] and puts it on the byte[] parameter.
     * @param obj the object to extract the reference
     * @param bytes the byte[] to write the object reference
     * @param offset the offset to use
     */
    default void toByteArray(Object obj, byte[] bytes, int offset) {
        System.arraycopy(toByteArray(obj), 0, bytes, offset, getReferenceSize());
    }

}
