package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/**
 * Should be used on deserialize procedure
 */
public final class ObjectFromBytesStateInfo implements IStateInfo {

    private final Class<?> clazz;
    private final Function<byte[], Object> resolver;
    private final byte[] bytes;
    private int offset = 0;
    private final int referenceSize;
    private final byte[] mutableIndexes, immutableIndexes;

    public ObjectFromBytesStateInfo(byte[] bytes, int offset, Class<?> clazz, Function<byte[], Object> resolver, int referenceSize) {
        this(bytes, offset, clazz, resolver, referenceSize, null, null);
    }

    public ObjectFromBytesStateInfo(byte[] bytes, int offset, Class<?> clazz, Function<byte[], Object> resolver, int referenceSize, byte[] mutableIndexes, byte[] immutableIndexes) {
        this.clazz = clazz;
        this.resolver = resolver;
        this.bytes = bytes;
        this.referenceSize = referenceSize;
        this.mutableIndexes = mutableIndexes;
        this.immutableIndexes = immutableIndexes;
        this.offset = offset;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public int getInt() throws ManipulatorException {
        return ByteConverter.getInt(bytes, advance(ByteConverter.INT_BYTE_SIZE));
    }

    private int advance(int byteNumber) {
        int index = offset;
        offset += byteNumber;
        return index;
    }

    @Override
    public long getLong() throws ManipulatorException {
        return ByteConverter.getLong(bytes, advance(ByteConverter.LONG_BYTE_SIZE));
    }

    @Override
    public float getFloat() throws ManipulatorException {
        return ByteConverter.getFloat(bytes, advance(ByteConverter.FLOAT_BYTE_SIZE));
    }

    @Override
    public double getDouble() throws ManipulatorException {
        return ByteConverter.getDouble(bytes, advance(ByteConverter.DOUBLE_BYTE_SIZE));
    }

    @Override
    public char getChar() throws ManipulatorException {
        return ByteConverter.getChar(bytes, advance(ByteConverter.CHAR_BYTE_SIZE));
    }

    @Override
    public short getShort() throws ManipulatorException {
        return ByteConverter.getShort(bytes, advance(ByteConverter.SHORT_BYTE_SIZE));
    }

    @Override
    public byte getByte() throws ManipulatorException {
        return ByteConverter.getByte(bytes, advance(ByteConverter.BYTE_BYTE_SIZE));
    }

    @Override
    public boolean getBoolean() throws ManipulatorException {
        return ByteConverter.getBoolean(bytes, advance(ByteConverter.BOOLEAN_BYTE_SIZE));
    }

    @Nullable
    @Override
    public Object getObject() throws ManipulatorException {
        byte[] ref = new byte[referenceSize];
        System.arraycopy(bytes, advance(referenceSize), ref, 0, referenceSize);
        return resolver.apply(ref);
    }

    @Override
    public byte[] asBytes() throws ManipulatorException {
        return bytes;
    }

    @Override
    public byte[] immutableIndexes() throws ManipulatorException {
        return immutableIndexes;
    }

    @Override
    public Optional<byte[]> mutableIndexes() throws ManipulatorException {
        return Optional.ofNullable(mutableIndexes);
    }
}
