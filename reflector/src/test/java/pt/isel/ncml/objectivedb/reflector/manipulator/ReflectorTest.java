package pt.isel.ncml.objectivedb.reflector.manipulator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.test.benchmark.BenchMaker;
import pt.isel.ncml.objectivedb.test.model.*;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by nuno on 4/1/17.
 */
@RunWith(Parameterized.class)
public class ReflectorTest {

    private final IFieldManipulatorFactory reflector;
    private final int warm = 5_000_000;
    private final int run = 10_000_000;

    public ReflectorTest(IFieldManipulatorFactory reflector) {
        this.reflector = reflector;
    }

    @Parameterized.Parameters
    public static Iterable<IFieldManipulatorFactory[]> getReflectors() {
        return Arrays.<IFieldManipulatorFactory[]>asList(
                new IFieldManipulatorFactory[]{new MethodHandleFactory(new MockResolver(), new ConfigIndexes())}
                ,new IFieldManipulatorFactory[]{new ReflectionFactory(new MockResolver(), new ConfigIndexes())}
        );
    }

    @Test
    public void getSetSingleState() throws Throwable {

        PInt intClass = new PInt(25448);
        PLong longClass = new PLong(-25486662545L);
        PFloat floatClass = new PFloat(253.215F);
        PDouble doubleClass = new PDouble(-254866.62545D);
        PChar charClass = new PChar('!');
        PShort shortClass = new PShort((short)1145);
        PBoolean booleanClass = new PBoolean(true);
        PByte byteClass = new PByte((byte) 0b00110010);

        PInt intClass2 = new PInt(0);
        PLong longClass2 = new PLong(0);
        PFloat floatClass2 = new PFloat(0);
        PDouble doubleClass2 = new PDouble(0);
        PChar charClass2 = new PChar((char)0);
        PShort shortClass2 = new PShort((short)0);
        PBoolean booleanClass2 = new PBoolean(false);
        PByte byteClass2 = new PByte((byte) 0);

        Object[] original = {intClass, longClass, floatClass, doubleClass, charClass, shortClass, booleanClass, byteClass};
        Object[] blank = {intClass2, longClass2, floatClass2, doubleClass2, charClass2, shortClass2, booleanClass2, byteClass2};

        for (int i = 0; i < 8; i++) {
            Object from = original[i];
            Object to = blank[i];
            Class<?> aClass = from.getClass();
            IFieldStateManipulator fieldManipulator = reflector.getFieldManipulator(aClass);
            String testName = String.format("getSetState for class %s and reflector %s", aClass.getSimpleName(), reflector.getClass().getSimpleName());
            new BenchMaker(TimeUnit.MICROSECONDS)
                    .addTest(testName, warm, run, () -> {
                        IStateInfo stateInfo = fieldManipulator.getStateInfo(from);
                        fieldManipulator.setStateInfo(to, stateInfo);
                    })
                    .benchmark();
        }

        assertEquals(intClass.getValue(), intClass2.getValue());
        assertEquals(longClass.getValue(), longClass2.getValue());
        assertEquals(floatClass.getValue(), floatClass2.getValue(),0.0);
        assertEquals(doubleClass.getValue(), doubleClass2.getValue(),0.0);
        assertEquals(charClass.getValue(), charClass2.getValue());
        assertEquals(shortClass.getValue(), shortClass2.getValue());
        assertEquals(booleanClass.getValue(), booleanClass2.getValue());
        assertEquals(byteClass.getValue(), byteClass2.getValue());



    }

    @Test
    public void getSetStateAllPrimities() throws Throwable {
        IFieldStateManipulator manipulator = reflector.getFieldManipulator(AllPrimitives.class);

        AllPrimitives primitive = new AllPrimitives(25448, -21, 2.5f, -6.2d, (byte) 246, 's', (short) 7, true);
        AllPrimitives another = new AllPrimitives(0,0, 0f, 0d, (byte) 0, (char) 0, (short) 0, false);

        new BenchMaker(TimeUnit.MILLISECONDS)
                .addTest("getSetStateAllPrimities " + reflector.getClass().getSimpleName(), warm, run, () -> {
                    IStateInfo stateInfo = manipulator.getStateInfo(primitive);
                    manipulator.setStateInfo(another, stateInfo);
                })
                .benchmark();

        assertEquals(primitive.getI(), another.getI());
        assertEquals(primitive.getL(), another.getL());
        assertEquals(primitive.getF(), another.getF(), 0.0);
        assertEquals(primitive.getD(), another.getD(), 0.0);
        assertEquals(primitive.getB(), another.getB());
        assertEquals(primitive.getC(), another.getC());
        assertEquals(primitive.getS(), another.getS());
        assertEquals(primitive.getBl(), another.getBl());

    }

    @Test
    public void fromBytes() throws Exception {
        IFieldStateManipulator manipulator = reflector.getFieldManipulator(AllPrimitives.class);

        AllPrimitives primitive = new AllPrimitives(25448, -21, 2.5f, -6.2d, (byte) 246, 's', (short) 7, true);
        AllPrimitives another = new AllPrimitives(0,0, 0f, 0d, (byte) 0, (char) 0, (short) 0, false);

        new BenchMaker(TimeUnit.MILLISECONDS)
                .addTest("fromBytes " + reflector.getClass().getSimpleName(), warm, run, () -> {
                    IStateInfo stateInfo = manipulator.getStateInfo(primitive);
                    IStateInfo byteInfo = new ObjectFromBytesStateInfo(stateInfo.asBytes(), 0, AllPrimitives.class, null, 0);
                    manipulator.setStateInfo(another, byteInfo);
                })
                .benchmark();

        assertEquals(primitive.getI(), another.getI());
        assertEquals(primitive.getL(), another.getL());
        assertEquals(primitive.getF(), another.getF(), 0.0);
        assertEquals(primitive.getD(), another.getD(), 0.0);
        assertEquals(primitive.getB(), another.getB());
        assertEquals(primitive.getC(), another.getC());
        assertEquals(primitive.getS(), another.getS());
        assertEquals(primitive.getBl(), another.getBl());
    }

    @Test
    public void testDefaultClassNameIndex() throws Exception {
        IFieldStateManipulator manipulator = reflector.getFieldManipulator(AllPrimitives.class);
        AllPrimitives primitive = new AllPrimitives(25448, -21, 2.5f, -6.2d, (byte) 246, 's', (short) 7, true);
        IStateInfo stateInfo = manipulator.getStateInfo(primitive);
        assertTrue(!stateInfo.mutableIndexes().isPresent());
        assertNotNull(stateInfo.immutableIndexes());
        assertArrayEquals(ByteConverter.fromObject(AllPrimitives.class), stateInfo.immutableIndexes());
    }
}