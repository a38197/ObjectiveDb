package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import java.util.Arrays;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class ShortArrayStateInfo implements IArrayStateInfo {

    public static ShortArrayStateInfo from(short[] array) {
        return new ToBytes(array);
    }


    @Override
    public Class<?> getClazz() {
        return short[].class;
    }

    private static class ToBytes extends ShortArrayStateInfo {

        private final short[] array;
        private int index = 0;

        private ToBytes(short[] array) {
            this.array = array;
        }

        @Override
        public short getShort() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromShortArray(array);
        }
    }


    private static class FromBytes extends ShortArrayStateInfo {

        private final byte[] bytes;
        private int offset;

        private FromBytes(byte[] bytes, int offset) {
            this.bytes = bytes;
            this.offset = offset;
        }

        @Override
        public short getShort() throws ManipulatorException {
            final int index = offset;
            offset += ByteConverter.SHORT_BYTE_SIZE;
            return ByteConverter.getShort(bytes, index);
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            if(0 == offset)
                return bytes;

            return Arrays.copyOfRange(bytes, offset, bytes.length - offset);
        }
    }
}
