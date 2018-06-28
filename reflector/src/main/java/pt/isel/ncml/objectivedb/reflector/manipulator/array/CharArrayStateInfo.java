package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class CharArrayStateInfo implements IArrayStateInfo {

    public static CharArrayStateInfo from(char[] array) {
        return new ToBytes(array);
    }

    @Override
    public Class<?> getClazz() {
        return char[].class;
    }

    private static class ToBytes extends CharArrayStateInfo {

        private final char[] array;
        private int index = 0;

        private ToBytes(char[] array) {
            this.array = array;
        }

        @Override
        public char getChar() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromCharArray(array);
        }
    }

}
