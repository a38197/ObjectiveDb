package pt.isel.ncml.objectivedb.reflector;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class MiscelaneousTest {

    @Test
    public void changeBoxedPrimitiveValue() throws Exception {
        Integer i1 = 1;
        Container<Integer> container = new Container<>(i1);
        final Field field = Integer.class.getDeclaredField("value");
        field.setAccessible(true);
        field.setInt(i1, 5);
        assertEquals(5, container.value.intValue());
        assertEquals(5, i1.intValue());
    }

    private static class Container<T>{
        private final T value;

        public Container(T value) {
            this.value = value;
        }
    }
}
