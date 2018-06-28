package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

public interface IFieldManipulatorFactory {

    /**
     * Gets an entity capable of getting the internal state of and object and restoring it.
     * This method is generic just for commodity. Usually there frameworks work with Object but if, for some reason, we
     * want to work with a concrete class only, its possible. If not just use raw types.
     * @param objClass the class to manipulate
     * @return a field manipulator for the required class
     * @throws ManipulatorException if the field manipulator creation fails
     */
    IFieldStateManipulator getFieldManipulator(Class<?> objClass) throws ManipulatorException;

}
