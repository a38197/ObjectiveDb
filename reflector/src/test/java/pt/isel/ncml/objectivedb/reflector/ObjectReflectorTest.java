package pt.isel.ncml.objectivedb.reflector;

import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.mockito.Mockito;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;
import pt.isel.ncml.objectivedb.reflector.manipulator.IStateInfo;
import pt.isel.ncml.objectivedb.reflector.manipulator.ObjectFromBytesStateInfo;

import java.lang.reflect.Array;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class ObjectReflectorTest {

    @Test
    public void reflectCharArray() throws Exception {
        final IObjectResolver mock = Mockito.mock(IObjectResolver.class);
        Mockito.when(mock.toByteArray(any())).thenReturn(new byte[]{});
        ObjectReflector reflector = new ObjectReflector(new MethodHandlesMetaStorage(mock, new ConfigIndexes()));
        final IReflectedObject reflect = reflector.reflect("some string");

        FluentIterable<IReflectedObject> from = FluentIterable.from(reflect.innerReflectedObjects());
        assertEquals(1, from.size());

        final IReflectedObject iReflectedObject = from.get(0);
        final char[] object = ((char[]) iReflectedObject.getObject());
        final IStateInfo stateInfo = iReflectedObject.fieldManipulator().getStateInfo(object);
        final byte[] bytes = stateInfo.asBytes();

        final ObjectFromBytesStateInfo info = new ObjectFromBytesStateInfo(bytes, 0, null,null, 0);
        for (int i = 0; i < object.length ; i++) {
            assertEquals(object[i], info.getChar());
        }

        iReflectedObject.innerReflectedObjects().forEach(iReflectedObject1 -> {});
    }

    @Test
    public void arrayInstantiationTest() throws Exception {
        int[][][] array = new int[1][2][3];
        final Object o = Array.newInstance(array.getClass().getComponentType(), 1);
        int[][][] newArray = (int[][][]) o;
        newArray = newArray;
    }
}