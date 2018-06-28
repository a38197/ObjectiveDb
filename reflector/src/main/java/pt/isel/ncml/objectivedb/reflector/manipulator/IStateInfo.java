package pt.isel.ncml.objectivedb.reflector.manipulator;

/**
 * Created by nuno on 3/31/17.
 */

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents the state of an object. Its a one time use object by default.
 * Its intended use is to, in the correct order, execute the get operations to obtain its primary field data.
 */
public interface IStateInfo {
    Class<?> getClazz();
    int getInt() throws ManipulatorException;
    long getLong() throws ManipulatorException;
    float getFloat() throws ManipulatorException;
    double getDouble() throws ManipulatorException;
    char getChar() throws ManipulatorException;
    short getShort() throws ManipulatorException;
    byte getByte() throws ManipulatorException;
    boolean getBoolean() throws ManipulatorException;

    /**
     * Returns (possibly searches cache then database) for an object of this representation.
     * Its expected that this may put in cache several objects recursively since the object graph may be quite large
     * Should be a special value for NULL for null values and deleted objects.
     * @return
     * @throws ManipulatorException
     */
    @Nullable
    Object getObject() throws ManipulatorException;

    /**
     * The ordered byte array containing the state of the variable. This array is the state that should be stored.
     * @return
     * @throws ManipulatorException
     */
    byte[] asBytes() throws ManipulatorException;

    /**
     * There is at least one mandatory automatic index, the class name.
     * @return the indexes as bytes
     * @throws ManipulatorException
     */
    byte[] immutableIndexes() throws ManipulatorException;

    /**
     * Optional mutable indexes. This indexes will be managed in memory.
     * @return the indexes as bytes
     * @throws ManipulatorException
     */
    Optional<byte[]> mutableIndexes() throws ManipulatorException;
}
