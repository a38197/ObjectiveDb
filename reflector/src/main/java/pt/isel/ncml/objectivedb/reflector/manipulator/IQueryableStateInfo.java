package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

/**
 * For queries
 */
public interface IQueryableStateInfo extends IStateInfo {
    int getInt(String fieldName) throws ManipulatorException;
    long getLong(String fieldName) throws ManipulatorException;
    float getFloat(String fieldName) throws ManipulatorException;
    double getDouble(String fieldName) throws ManipulatorException;
    char getChar(String fieldName) throws ManipulatorException;
    short getShort(String fieldName) throws ManipulatorException;
    byte getByte(String fieldName) throws ManipulatorException;
    boolean getBoolean(String fieldName) throws ManipulatorException;
}
