package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.ClassNameIndex;
import pt.isel.ncml.objectivedb.reflector.manipulator.IStateInfo;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * <p>{@link IStateInfo} specialized in arrays. Each array type will only allow to retrieve its specific type.</p>
 * <p>Could be a generic interface, but would loose performance because all primitives needed to be wrapped.</p>
 */
public interface IArrayStateInfo extends IStateInfo {

    @Override
    default int getInt() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default long getLong() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default float getFloat() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default double getDouble() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default char getChar() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default short getShort() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default byte getByte() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean getBoolean() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    default Object getObject() throws ManipulatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    default byte[] immutableIndexes() throws ManipulatorException{
        return ClassNameIndex.fromClassName(getClazz());
    }

    @Override
    default Optional<byte[]> mutableIndexes() throws ManipulatorException {
        return Optional.empty();
    }
}
