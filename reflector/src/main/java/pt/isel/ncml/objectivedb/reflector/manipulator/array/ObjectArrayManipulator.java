package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;
import pt.isel.ncml.objectivedb.reflector.manipulator.IStateInfo;

/**
 * Created by nuno on 5/7/17.
 */
public class ObjectArrayManipulator implements IFieldStateManipulator {

    private final IObjectResolver resolver;

    public ObjectArrayManipulator(IObjectResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public IStateInfo getStateInfo(Object instance) throws ManipulatorException {
        return ObjectArrayStateInfo.from((Object[]) instance, resolver);
    }

    @Override
    public void setStateInfo(Object instance, IStateInfo info) throws ManipulatorException {
        Object[] array = (Object[]) instance;
        for (int i = 0; i < array.length; i++) {
            array[i] = info.getObject();
        }
    }
}
