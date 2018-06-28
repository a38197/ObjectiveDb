package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class ByteArrayStateInfo implements IArrayStateInfo {

    public static ByteArrayStateInfo from(byte[] array) {
        return new ToBytes(array);
    }

    @Override
    public Class<?> getClazz() {
        return byte[].class;
    }

    private static class ToBytes extends ByteArrayStateInfo {

        private final byte[] array;
        private int index = 0;

        private ToBytes(byte[] array) {
            this.array = array;
        }

        @Override
        public byte getByte() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromByteArray(array);
        }
    }

}
