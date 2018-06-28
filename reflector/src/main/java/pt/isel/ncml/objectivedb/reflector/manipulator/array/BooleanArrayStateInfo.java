package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class BooleanArrayStateInfo implements IArrayStateInfo {

    public static BooleanArrayStateInfo from(boolean[] array) {
        return new ToBytes(array);
    }

    @Override
    public Class<?> getClazz() {
        return boolean[].class;
    }

    private static class ToBytes extends BooleanArrayStateInfo {

        private final boolean[] array;
        private int index = 0;

        private ToBytes(boolean[] array) {
            this.array = array;
        }

        @Override
        public boolean getBoolean() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromBooleanArray(array);
        }
    }

}
