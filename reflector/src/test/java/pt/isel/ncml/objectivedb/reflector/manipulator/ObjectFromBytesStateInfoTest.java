package pt.isel.ncml.objectivedb.reflector.manipulator;

import org.junit.Test;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.test.model.AllPrimitives;

import static org.junit.Assert.assertEquals;

/**
 * Created by nuno on 5/7/17.
 */
public class ObjectFromBytesStateInfoTest {
    @Test
    public void testFromBytes() throws Exception {

        AllPrimitives value = new AllPrimitives(123456, 321654L, 147258F, 963852D, (byte)0b10010101, 'd', (short) 1598, true);
        AllPrimitives empty = new AllPrimitives(0, 0L, 0F, 0D, (byte)0b00000000, '0', (short) 0, false);

        final MockResolver resolver = new MockResolver();
        final MethodHandleFactory factory = new MethodHandleFactory(resolver, new ConfigIndexes());
        final IFieldStateManipulator fieldManipulator = factory.getFieldManipulator(AllPrimitives.class);
        final IStateInfo stateInfo = fieldManipulator.getStateInfo(value);
        final byte[] bytes = stateInfo.asBytes();

        final ObjectFromBytesStateInfo bytesStateInfo = new ObjectFromBytesStateInfo(bytes, 0, AllPrimitives.class, null, 0);
        fieldManipulator.setStateInfo(empty, bytesStateInfo);
        assertEquals(value.getI(), empty.getI());
        assertEquals(value.getL(), empty.getL());
        assertEquals(value.getF(), empty.getF(), 0F);
        assertEquals(value.getD(), empty.getD(), 0D);
        assertEquals(value.getB(), empty.getB());
        assertEquals(value.getC(), empty.getC());
        assertEquals(value.getS(), empty.getS());
        assertEquals(value.getBl(), empty.getBl());

    }
}