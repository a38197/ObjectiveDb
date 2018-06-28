package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

/**
 * Created by nuno on 4/1/17.
 */
interface IFieldStateSetter {
    void set(Object instance, IStateInfo stateInfo) throws ManipulatorException;
}
