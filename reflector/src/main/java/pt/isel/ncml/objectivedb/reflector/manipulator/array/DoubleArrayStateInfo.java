package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import java.util.Arrays;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class DoubleArrayStateInfo implements IArrayStateInfo {

    public static DoubleArrayStateInfo from(double[] array) {
        return new ToBytes(array);
    }

    @Override
    public Class<?> getClazz() {
        return double[].class;
    }

    private static class ToBytes extends DoubleArrayStateInfo {

        private final double[] array;
        private int index = 0;

        private ToBytes(double[] array) {
            this.array = array;
        }

        @Override
        public double getDouble() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromDoubleArray(array);
        }
    }


    private static class FromBytes extends DoubleArrayStateInfo {

        private final byte[] bytes;
        private int offset;

        private FromBytes(byte[] bytes, int offset) {
            this.bytes = bytes;
            this.offset = offset;
        }

        @Override
        public double getDouble() throws ManipulatorException {
            final int index = offset;
            offset += ByteConverter.DOUBLE_BYTE_SIZE;
            return ByteConverter.getDouble(bytes, index);
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            if(0 == offset)
                return bytes;

            return Arrays.copyOfRange(bytes, offset, bytes.length - offset);
        }
    }
}
