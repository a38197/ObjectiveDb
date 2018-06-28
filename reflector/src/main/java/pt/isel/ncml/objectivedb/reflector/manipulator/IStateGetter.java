package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

/**
 * Created by nuno on 4/1/17.
 */
interface IStateGetter {
    /**
     * One shot use to setStateInfo
     * @param instance object to extract information
     * @return a state extractor for the input object
     * @throws ManipulatorException if the state could not be get
     */
    IStateInfo getStateInfo(Object instance) throws ManipulatorException;
}
