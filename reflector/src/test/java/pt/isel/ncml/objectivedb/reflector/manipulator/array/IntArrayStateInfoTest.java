package pt.isel.ncml.objectivedb.reflector.manipulator.array;

import org.junit.Test;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import static org.junit.Assert.assertEquals;
import static pt.isel.ncml.objectivedb.util.ByteConverter.INT_BYTE_SIZE;

/**
 * Created by nuno on 5/7/17.
 */
public class IntArrayStateInfoTest {

    @Test
    public void convertToBytes() throws Throwable {
        int[] original = {1,2,3,4,5,6,7,8,9,10};
        IntArrayStateInfo stateInfo = IntArrayStateInfo.from(original);
        final byte[] bytes = stateInfo.asBytes();
        assertEquals(INT_BYTE_SIZE * original.length, bytes.length);

        for (int i = 0 ; i < original.length; i++) {
            byte[] intAsBytes = new byte[INT_BYTE_SIZE];
            System.arraycopy(bytes, i * INT_BYTE_SIZE, intAsBytes, 0, INT_BYTE_SIZE);
            final int recovered = ByteConverter.getInt(intAsBytes);
            assertEquals(original[i], recovered);
        }
    }

}