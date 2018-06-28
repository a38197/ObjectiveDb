package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class LongArrayStateInfo implements IArrayStateInfo {

    public static LongArrayStateInfo from(long[] array) {
        return new ToBytes(array);
    }

    @Override
    public Class<?> getClazz() {
        return long[].class;
    }

    private static class ToBytes extends LongArrayStateInfo {

        private final long[] array;
        private int index = 0;

        private ToBytes(long[] array) {
            this.array = array;
        }

        @Override
        public long getLong() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromLongArray(array);
        }
    }

}
