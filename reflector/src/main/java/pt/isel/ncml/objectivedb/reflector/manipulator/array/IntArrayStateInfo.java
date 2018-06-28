package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class IntArrayStateInfo implements IArrayStateInfo {

    public static IntArrayStateInfo from(int[] array) {
        return new ToBytes(array);
    }


    @Override
    public Class<?> getClazz() {
        return int[].class;
    }

    private static class ToBytes extends IntArrayStateInfo {

        private final int[] array;
        private int index = 0;

        private ToBytes(int[] array) {
            this.array = array;
        }

        @Override
        public int getInt() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromIntArray(array);
        }
    }

}
