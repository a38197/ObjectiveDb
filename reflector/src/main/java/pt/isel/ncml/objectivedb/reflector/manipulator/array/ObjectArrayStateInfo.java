package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;

/**
 * Created by nuno on 5/7/17.
 */
public abstract class ObjectArrayStateInfo implements IArrayStateInfo {

    public static ObjectArrayStateInfo from(Object[] array, IObjectResolver resolver) {
        return new ToBytes(array, resolver);
    }

    private static class ToBytes extends ObjectArrayStateInfo {

        private final Object[] array;
        private final IObjectResolver resolver;
        private int index = 0;

        private ToBytes(Object[] array, IObjectResolver resolver) {
            this.array = array;
            this.resolver = resolver;
        }

        @Override
        public Class<?> getClazz() {
            return array.getClass();
        }

        @Override
        public Object getObject() throws ManipulatorException {
            return array[index++];
        }

        @Override
        public byte[] asBytes() throws ManipulatorException {
            return convertToBytes();
        }

        private byte[] convertToBytes() {
            byte[] bytes = new byte[array.length * resolver.getReferenceSize()];
            int offset = 0;
            for (Object o : array) {
                resolver.toByteArray(o, bytes, offset);
                offset += resolver.getReferenceSize();
            }
            return bytes;
        }
    }

}
