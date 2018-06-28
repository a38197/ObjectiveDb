package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import com.google.common.base.Preconditions;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;
import pt.isel.ncml.objectivedb.reflector.manipulator.IStateInfo;

/**
 * Created by nuno on 5/7/17.
 */
public class ByteArrayManipulator implements IFieldStateManipulator {

    @Override
    public IStateInfo getStateInfo(Object instance) throws ManipulatorException {
        Preconditions.checkArgument(instance instanceof byte[], "Wrong object instance");

        return ByteArrayStateInfo.from((byte[]) instance);
    }

    @Override
    public void setStateInfo(Object instance, IStateInfo info) throws ManipulatorException {
        Preconditions.checkArgument(instance instanceof byte[], "Wrong object instance");

        byte[] array = (byte[]) instance;
        for (int i = 0; i < array.length; i++) {
            array[i] = info.getByte();
        }
    }
}
