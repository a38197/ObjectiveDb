package pt.isel.ncml.objectivedb.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * <p>Utility class for byte conversions. Provides methods to use for converting from and to bytes both with or without in place sets</p>
 * <p>A copy of {@link java.io.Bits} was used to make bit conversions</p>
 */
public class ByteConverter {

    public static final int INT_BYTE_SIZE = 4;
    public static final int FLOAT_BYTE_SIZE = 4;
    public static final int LONG_BYTE_SIZE = 8;
    public static final int DOUBLE_BYTE_SIZE = 8;
    public static final int CHAR_BYTE_SIZE = 2;
    public static final int SHORT_BYTE_SIZE = 2;
    public static final int BOOLEAN_BYTE_SIZE = 1;
    public static final int BYTE_BYTE_SIZE = 1;

    private ByteConverter() {}

    public static int getInt(byte[] input){
        return getInt(input, 0);
    }

    public static int getInt(byte[] input, int offset) {
        return Bits.getInt(input, offset);
    }

    public static long getLong(byte[] bytes) {
        return getLong(bytes, 0);
    }

    public static long getLong(byte[] bytes, int offset) {
        return Bits.getLong(bytes, offset);
    }

    public static float getFloat(byte[] input) {
        return getFloat(input, 0);
    }

    public static float getFloat(byte[] input, int offset) {
        return Bits.getFloat(input, offset);
    }

    public static double getDouble(byte[] input) {
        return getDouble(input, 0);
    }

    public static double getDouble(byte[] input, int offset) {
        return Bits.getDouble(input, offset);
    }

    public static char getChar(byte[] input) {
        return getChar(input, 0);
    }

    public static char getChar(byte[] input, int offset) {
        return Bits.getChar(input, offset);
    }

    public static short getShort(byte[] bytes) {
        return getShort(bytes, 0);
    }

    public static short getShort(byte[] bytes, int offset) {
        return Bits.getShort(bytes, offset);
    }

    public static byte getByte(byte[] input){
        return input[0];
    }

    public static byte getByte(byte[] input, int offset) {
        return input[offset];
    }

    public static boolean getBoolean(byte[] input) {
        return getBoolean(input, 0);
    }

    public static boolean getBoolean(byte[] input, int offset) {
        return Bits.getBoolean(input, offset);
    }

    public static byte[] fromInt(int input) {
        byte[] bytes = new byte[INT_BYTE_SIZE];
        fromInt(input, bytes, 0);
        return bytes;
    }

    public static void fromInt(int input, byte[] bytes, int offset) {
        Bits.putInt(bytes, offset, input);
    }

    public static byte[] fromLong(long input) {
        byte[] bytes = new byte[LONG_BYTE_SIZE];
        fromLong(input, bytes, 0);
        return bytes;
    }

    public static void fromLong(long input, byte[] bytes, int offset) {
        Bits.putLong(bytes, offset, input);
    }

    public static byte[] fromFloat(float input) {
        return fromInt(Float.floatToRawIntBits(input));
    }

    public static void fromFloat(float input, byte[] bytes, int offset) {
        Bits.putFloat(bytes, offset, input);
    }

    public static byte[] fromDouble(double input) {
        return fromLong(Double.doubleToRawLongBits(input));
    }

    public static void fromDouble(double input, byte[] bytes, int offset) {
        Bits.putDouble(bytes, offset, input);
    }

    public static byte[] fromChar(char input) {
        final byte[] bytes = new byte[CHAR_BYTE_SIZE];
        fromChar(input, bytes, 0);
        return bytes;
    }

    public static void fromChar(char input, byte[] bytes, int offset) {
        Bits.putChar(bytes, offset, input);
    }

    public static byte[] fromShort(short input) {
        byte[] bytes = new byte[SHORT_BYTE_SIZE];
        fromShort(input, bytes, 0);
        return bytes;
    }

    public static void fromShort(short input, byte[] bytes, int offset) {
        Bits.putShort(bytes, offset, input);
    }

    public static byte[] fromByte(byte input) {
        final byte[] bytes = new byte[BYTE_BYTE_SIZE];
        fromByte(input, bytes, 0);
        return bytes;
    }

    public static void fromByte(byte input, byte[] bytes, int offset) {
        bytes[offset] = input;
    }

    public static byte[] fromBoolean(boolean input) {
        final byte[] bytes = new byte[BOOLEAN_BYTE_SIZE];
        fromBoolean(input, bytes, 0);
        return bytes;
    }

    public static void fromBoolean(boolean input, byte[] bytes, int offset) {
        Bits.putBoolean(bytes, offset, input);
    }

    private static final Object2ByteData OBJ_FUN = new Object2ByteData(
            (i, b, o) -> fromString(String.valueOf(i), b, o),
            (o) -> String.valueOf(o).length() * CHAR_BYTE_SIZE);

    private static final Object2ByteData CLASS_FUN = new Object2ByteData(
            (i, b, o) -> getClassNameForIndex(((Class)i), b, o),
            (i) -> (getClassNameBufferSizeForIndex((Class) i)));

    /**
     * Returns a class name with invalid className characters to avoid similar class name issues.
     * @param clazz
     * @return
     */
    private static void getClassNameForIndex(Class clazz, byte[] buffer, int offset) {
        String name = clazz.getName();
        fromString(name, buffer, offset);
        fromChar('!', buffer, offset + name.length() * CHAR_BYTE_SIZE);
    }

    private static int getClassNameBufferSizeForIndex(Class clazz) {
        return clazz.getName().length() * CHAR_BYTE_SIZE + 2;
    }

    private static final Map<Class<?>, Object2ByteData> OBJ_2_BYTE = ImmutableMap.<Class<?>, Object2ByteData>builder()
            .put(Integer.class, new Object2ByteData((i, b, o) -> fromInt(((int) i), b, o) , (o) -> INT_BYTE_SIZE))
            .put(Long.class, new Object2ByteData((i, b, o) -> fromLong(((long) i), b, o), (o) -> LONG_BYTE_SIZE ))
            .put(Short.class, new Object2ByteData((i, b, o) -> fromShort(((short) i), b, o), (o) -> SHORT_BYTE_SIZE ))
            .put(Byte.class, new Object2ByteData((i, b, o) -> fromByte(((byte) i), b, o), (o) -> BYTE_BYTE_SIZE ))
            .put(Boolean.class, new Object2ByteData((i, b, o) -> fromBoolean(((boolean) i), b, o), (o) -> BOOLEAN_BYTE_SIZE ))
            .put(Float.class, new Object2ByteData((i, b, o) -> fromFloat(((float) i), b, o), (o) -> FLOAT_BYTE_SIZE ))
            .put(Double.class, new Object2ByteData((i, b, o) -> fromDouble(((double) i), b, o), (o) -> DOUBLE_BYTE_SIZE ))
            .put(Character.class, new Object2ByteData((i, b, o) -> fromChar(((char) i), b, o), (o) -> CHAR_BYTE_SIZE ))
            .put(Class.class, CLASS_FUN)
            .build();

    private interface Object2ByteConvertCall {
        void call(Object input, byte[] bytes, int offset);
    }

    private interface BufferSizeFunc {
        int getSize(Object instance);
    }

    /**
     * Holds data for object to byte serialization and byte buffer size processing
     */
    private static class Object2ByteData implements Object2ByteConvertCall {

        private final Object2ByteConvertCall call;
        private final BufferSizeFunc byteByfferSize;

        private Object2ByteData(Object2ByteConvertCall call, BufferSizeFunc byteByfferSize) {
            this.call = call;
            this.byteByfferSize = byteByfferSize;
        }

        @Override
        public void call(Object input, byte[] bytes, int offset) {
            call.call(input, bytes, offset);
        }
    }

    /**
     * Converts to bytes an Object. Primitive wrappers will use the specific overload.
     * Other objects will use String.valueOf(Object). Special case is Class descriptors
     * @param input
     * @param bytes
     * @param offset
     */
    public static void fromObject(Object input, byte[] bytes, int offset) {
        OBJ_2_BYTE.getOrDefault(input.getClass(), OBJ_FUN).call(input, bytes, offset);
    }

    public static byte[] fromObject(Object input) {
        Object2ByteData fun = OBJ_2_BYTE.getOrDefault(input.getClass(), OBJ_FUN);
        byte[] buffer = new byte[fun.byteByfferSize.getSize(input)];
        fun.call.call(input, buffer, 0);
        return buffer;
    }

    public static void fromObject(Iterable<Object> input, byte[] bytes, int offset) {
        int[] currOffset = {offset};
        input.forEach(o -> {
            fromObject(o, bytes, currOffset[0]);
            currOffset[0] += bufferSize(o);
        });
    }

    public static byte[] fromString(String input) {
        final byte[] buffer = new byte[bufferSize(input)];
        fromString(input, buffer, 0);
        return buffer;
    }

    public static void fromString(String input, byte[] bytes, int offset) {
        for (int i = 0; i < input.length(); i++) {
            fromChar(input.charAt(i), bytes, offset);
            offset+=CHAR_BYTE_SIZE;
        }
    }

    /**
     * Calculates the exact size for the buffer needed to collect the bytes
     * @param objects
     * @return
     */
    public static int bufferSize(Iterable<Object> objects){
        int[] accum = {0};
        objects.forEach(o -> accum[0] += bufferSize(o));
        return accum[0];
    }

    public static int bufferSize(Object object){
        return OBJ_2_BYTE.getOrDefault(object.getClass(), OBJ_FUN).byteByfferSize.getSize(object);
    }

    /**
     * Converts and int array[] to the corresponding byte[]. Uses {@link ByteConverter#fromInt(int, byte[], int)}.
     * Could be generified if Java supported primitive generics.
     * @param input int[] to transform
     * @return a new byte[] with the ints converted
     */
    public static byte[] fromIntArray(int[] input) {
        final byte[] bytes = new byte[input.length * INT_BYTE_SIZE];
        int offset = 0;
        for (int i : input) {
            fromInt(i, bytes, offset);
            offset += INT_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromLongArray(long[] input) {
        final byte[] bytes = new byte[input.length * LONG_BYTE_SIZE];
        int offset = 0;
        for (long i : input) {
            fromLong(i, bytes, offset);
            offset += LONG_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromFloatArray(float[] input) {
        final byte[] bytes = new byte[input.length * FLOAT_BYTE_SIZE];
        int offset = 0;
        for (float i : input) {
            fromFloat(i, bytes, offset);
            offset += FLOAT_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromDoubleArray(double[] input) {
        final byte[] bytes = new byte[input.length * DOUBLE_BYTE_SIZE];
        int offset = 0;
        for (double i : input) {
            fromDouble(i, bytes, offset);
            offset += DOUBLE_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromShortArray(short[] input) {
        final byte[] bytes = new byte[input.length * SHORT_BYTE_SIZE];
        int offset = 0;
        for (short i : input) {
            fromShort(i, bytes, offset);
            offset += SHORT_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromByteArray(byte[] input) {
        final byte[] bytes = new byte[input.length * BYTE_BYTE_SIZE];
        int offset = 0;
        for (byte i : input) {
            fromByte(i, bytes, offset);
            offset += BYTE_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromBooleanArray(boolean[] input) {
        final byte[] bytes = new byte[input.length * BOOLEAN_BYTE_SIZE];
        int offset = 0;
        for (boolean i : input) {
            fromBoolean(i, bytes, offset);
            offset += BOOLEAN_BYTE_SIZE;
        }
        return bytes;
    }

    public static byte[] fromCharArray(char[] input) {
        final byte[] bytes = new byte[input.length * CHAR_BYTE_SIZE];
        int offset = 0;
        for (char i : input) {
            fromChar(i, bytes, offset);
            offset += CHAR_BYTE_SIZE;
        }
        return bytes;
    }

    /**
     * Copy of {@link java.io.Bits}
     */
    private static final class Bits {

    /*
     * Methods for unpacking primitive values from byte arrays starting at
     * given offsets.
     */

        static boolean getBoolean(byte[] b, int off) {
            return b[off] != 0;
        }

        static char getChar(byte[] b, int off) {
            return (char) ((b[off + 1] & 0xFF) +
                    (b[off] << 8));
        }

        static short getShort(byte[] b, int off) {
            return (short) ((b[off + 1] & 0xFF) +
                    (b[off] << 8));
        }

        static int getInt(byte[] b, int off) {
            return ((b[off + 3] & 0xFF)      ) +
                    ((b[off + 2] & 0xFF) <<  8) +
                    ((b[off + 1] & 0xFF) << 16) +
                    ((b[off    ]       ) << 24);
        }

        static float getFloat(byte[] b, int off) {
            return Float.intBitsToFloat(getInt(b, off));
        }

        static long getLong(byte[] b, int off) {
            return ((b[off + 7] & 0xFFL)      ) +
                    ((b[off + 6] & 0xFFL) <<  8) +
                    ((b[off + 5] & 0xFFL) << 16) +
                    ((b[off + 4] & 0xFFL) << 24) +
                    ((b[off + 3] & 0xFFL) << 32) +
                    ((b[off + 2] & 0xFFL) << 40) +
                    ((b[off + 1] & 0xFFL) << 48) +
                    (((long) b[off])      << 56);
        }

        static double getDouble(byte[] b, int off) {
            return Double.longBitsToDouble(getLong(b, off));
        }

    /*
     * Methods for packing primitive values into byte arrays starting at given
     * offsets.
     */

        static void putBoolean(byte[] b, int off, boolean val) {
            b[off] = (byte) (val ? 1 : 0);
        }

        static void putChar(byte[] b, int off, char val) {
            b[off + 1] = (byte) (val      );
            b[off    ] = (byte) (val >>> 8);
        }

        static void putShort(byte[] b, int off, short val) {
            b[off + 1] = (byte) (val      );
            b[off    ] = (byte) (val >>> 8);
        }

        static void putInt(byte[] b, int off, int val) {
            b[off + 3] = (byte) (val       );
            b[off + 2] = (byte) (val >>>  8);
            b[off + 1] = (byte) (val >>> 16);
            b[off    ] = (byte) (val >>> 24);
        }

        static void putFloat(byte[] b, int off, float val) {
            putInt(b, off,  Float.floatToIntBits(val));
        }

        static void putLong(byte[] b, int off, long val) {
            b[off + 7] = (byte) (val       );
            b[off + 6] = (byte) (val >>>  8);
            b[off + 5] = (byte) (val >>> 16);
            b[off + 4] = (byte) (val >>> 24);
            b[off + 3] = (byte) (val >>> 32);
            b[off + 2] = (byte) (val >>> 40);
            b[off + 1] = (byte) (val >>> 48);
            b[off    ] = (byte) (val >>> 56);
        }

        static void putDouble(byte[] b, int off, double val) {
            putLong(b, off, Double.doubleToLongBits(val));
        }
    }
}
