package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

/**
 * Created by nuno on 4/1/17.
 */
class StateManipulator implements IFieldStateManipulator {

    private IStateGetter stateGetter;
    private IStateSetter stateSetter;

    public StateManipulator(IStateSetter stateSetter, IStateGetter stateBuilder) {
        this.stateSetter = stateSetter;
        this.stateGetter = stateBuilder;
    }

    @Override
    public IStateInfo getStateInfo(Object instance) throws ManipulatorException {
        return stateGetter.getStateInfo(instance);
    }

    @Override
    public void setStateInfo(Object instance, IStateInfo info) throws ManipulatorException {
        stateSetter.setStateInfo(instance, info);
    }
}
