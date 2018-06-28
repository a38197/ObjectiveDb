package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

public interface IFieldStateManipulator extends IStateSetter, IStateGetter {


    default IQueryableStateInfo getQueryableStateInfo(Object instance) throws ManipulatorException {throw new UnsupportedOperationException();}



}
