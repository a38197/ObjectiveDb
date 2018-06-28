package pt.isel.ncml.objectivedb.reflector;

import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;

import java.util.Collections;

/**
 * A wrapper to access internal state of the analysed object. This class does not address graph loops, it just exposes internal state.
 * Loops should be addressed on the consumer of this interface instances.
 */
public interface IReflectedObject {
    Class<?> getReflectedClass();
    IFieldStateManipulator fieldManipulator();
    Object getObject();
    Iterable<IReflectedObject> innerReflectedObjects();
    default boolean isNull(){
        return false;
    }

    /**
     * Reflected objects that have a null reference should be represented by this instance
     */
    IReflectedObject NULL = new IReflectedObject() {

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public Class<?> getReflectedClass() {
            throw new UnsupportedOperationException();
        }

        @Override
        public IFieldStateManipulator fieldManipulator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getObject() {
            return null;
        }

        @Override
        public Iterable<IReflectedObject> innerReflectedObjects() {
            return Collections.emptyList();
        }

    };
}
