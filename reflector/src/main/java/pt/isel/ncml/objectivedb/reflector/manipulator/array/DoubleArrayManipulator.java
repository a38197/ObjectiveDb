package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import com.google.common.base.Preconditions;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;
import pt.isel.ncml.objectivedb.reflector.manipulator.IStateInfo;

/**
 * Created by nuno on 5/7/17.
 */
public class DoubleArrayManipulator implements IFieldStateManipulator {

    @Override
    public IStateInfo getStateInfo(Object instance) throws ManipulatorException {
        Preconditions.checkArgument(instance instanceof double[], "Wrong object instance");

        return DoubleArrayStateInfo.from((double[]) instance);
    }

    @Override
    public void setStateInfo(Object instance, IStateInfo info) throws ManipulatorException {
        Preconditions.checkArgument(instance instanceof double[], "Wrong object instance");

        double[] array = (double[]) instance;
        for (int i = 0; i < array.length; i++) {
            array[i] = info.getDouble();
        }
    }
}
