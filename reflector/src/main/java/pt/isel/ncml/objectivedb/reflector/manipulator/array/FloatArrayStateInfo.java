package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class FloatArrayStateInfo implements IArrayStateInfo {

    public static FloatArrayStateInfo from(float[] array) {
        return new ToBytes(array);
    }

    @Override
    public Class<?> getClazz() {
        return float[].class;
    }

    private static class ToBytes extends FloatArrayStateInfo {

        private final float[] array;
        private int index = 0;

        private ToBytes(float[] array) {
            this.array = array;
        }

        @Override
        public float getFloat() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            return ByteConverter.fromFloatArray(array);
        }
    }

}
