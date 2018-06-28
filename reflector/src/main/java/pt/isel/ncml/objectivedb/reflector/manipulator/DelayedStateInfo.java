package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by nuno on 4/1/17.
 */
final class DelayedStateInfo implements IStateInfo {

    private final Object instance;
    private final Iterator<IFieldByteGetter> getterIterator;
    private final IIndexExtractor indexes;

    public DelayedStateInfo(Object instance, Iterator<IFieldByteGetter> getterIterator, IIndexExtractor indexes) {
        this.instance = instance;
        this.getterIterator = getterIterator;
        this.indexes = indexes;
    }

    @Override
    public Class<?> getClazz() {
        return instance.getClass();
    }

    @Override
    public int getInt() throws ManipulatorException {
        return ByteConverter.getInt(getterIterator.next().get(instance));
    }

    @Override
    public long getLong() throws ManipulatorException {
        return ByteConverter.getLong(getterIterator.next().get(instance));
    }

    @Override
    public float getFloat() throws ManipulatorException {
        return ByteConverter.getFloat(getterIterator.next().get(instance));
    }

    @Override
    public double getDouble() throws ManipulatorException {
        return ByteConverter.getDouble(getterIterator.next().get(instance));
    }

    @Override
    public char getChar() throws ManipulatorException {
        return ByteConverter.getChar(getterIterator.next().get(instance));
    }

    @Override
    public short getShort() throws ManipulatorException {
        return ByteConverter.getShort(getterIterator.next().get(instance));
    }

    @Override
    public byte getByte() throws ManipulatorException {
        return ByteConverter.getByte(getterIterator.next().get(instance));
    }

    @Override
    public boolean getBoolean() throws ManipulatorException {
        return ByteConverter.getBoolean(getterIterator.next().get(instance));
    }

    @Override
    public byte[] asBytes() throws ManipulatorException {
        return ManipulatorUtils.asBytes(this.getterIterator, instance);
    }

    @Override
    public Object getObject() throws ManipulatorException {
        throw new UnsupportedOperationException("You should use ObjectFromBytesStateInfo.class");
    }

    @Override
    public byte[] immutableIndexes() throws ManipulatorException {
        return indexes.immutableIndexes(instance);
    }

    @Override
    public Optional<byte[]> mutableIndexes() throws ManipulatorException {
        return Optional.ofNullable(indexes.mutableIndexes(instance));
    }
}
