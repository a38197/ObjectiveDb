package pt.isel.ncml.objectivedb.util.inspector;

import com.google.common.collect.Iterables;
import org.junit.Test;
import pt.isel.ncml.objectivedb.reflector.inspector.ClassInspector;

import static org.junit.Assert.assertEquals;

public class ClassInspectorTest {
    @Test
    public void getObjectReferences() throws Exception {
        ClassInspector inspector = new ClassInspector(String.class);
        String instance = "123456";
        final Iterable<Object> objectReferences = inspector.getObjectReferences(instance);
        final Object[] objects = Iterables.toArray(objectReferences, Object.class);
        assertEquals(1, objects.length);
    }

}