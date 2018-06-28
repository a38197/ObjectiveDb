package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

/**
 * Created by nuno on 4/1/17.
 */
interface IStateSetter {
    /**
     * Restores the state to the object passed as parameter
     * @param instance an object instance compatible with the {@link IStateInfo}
     * @param info a state info
     * @throws ManipulatorException if the state could not be stored
     */
    void setStateInfo(Object instance, IStateInfo info) throws ManipulatorException;
}
