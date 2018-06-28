package pt.isel.ncml.objectivedb.reflector.manipulator;

import com.google.common.collect.Lists;
import kotlin.collections.ArraysKt;
import org.junit.Before;
import org.junit.Test;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.index.IndexDefinition;
import pt.isel.ncml.objectivedb.test.model.AllPrimitives;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Nuno on 21/06/2017.
 */
public class IndexExtractionTest {

    private ConfigIndexes configIndexes = new ConfigIndexes();

    @Before
    public void setUp() throws Exception {
        //Indexes are extracted in alphabetical order because fields are discovered that way
        //Immutable indexes has className first
        configIndexes.putAll(AllPrimitives.class.getName(), Lists.newArrayList(
                new IndexDefinition("i", false),
                new IndexDefinition("c", false),
                new IndexDefinition("f", true),
                new IndexDefinition("d", true),
                new IndexDefinition("bl", true)
        ));
    }

    @Test
    public void mhIndex() throws Exception {
        MethodHandleFactory factory = new MethodHandleFactory(null, configIndexes);
        testIndex(factory);
    }

    @Test
    public void reflIndex() throws Exception {
        ReflectionFactory factory = new ReflectionFactory(null, configIndexes);
        testIndex(factory);
    }

    public void testIndex(IFieldManipulatorFactory factory) throws Exception {
        IFieldStateManipulator manipulator = factory.getFieldManipulator(AllPrimitives.class);
        AllPrimitives primitive = new AllPrimitives(25448, -21, 2.5f, -6.2d, (byte) 246, 's', (short) 7, true);
        IStateInfo stateInfo = manipulator.getStateInfo(primitive);
        byte[] immutableIndexes = stateInfo.immutableIndexes();
        byte[] classNameBytes = ByteConverter.fromObject(AllPrimitives.class);
        byte[] intBytes = ByteConverter.fromInt(primitive.getI());
        byte[] charBytes = ByteConverter.fromChar(primitive.getC());
        byte[] immutableIndexes2 = ArraysKt.plus(classNameBytes, ArraysKt.plus(charBytes, intBytes));
        assertArrayEquals(immutableIndexes, immutableIndexes2);

        byte[] mutableIndexes = stateInfo.mutableIndexes().get();
        byte[] floatBytes = ByteConverter.fromFloat(primitive.getF());
        byte[] doubleBytes = ByteConverter.fromDouble(primitive.getD());
        byte[] booleanBytes = ByteConverter.fromBoolean(primitive.getBl());
        byte[] mutableIndexes2 = ArraysKt.plus(booleanBytes, ArraysKt.plus(doubleBytes, floatBytes));
        assertArrayEquals(mutableIndexes, mutableIndexes2);
    }
}
