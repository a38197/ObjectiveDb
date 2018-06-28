package pt.isel.ncml.objectivedb.util.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by nuno on 4/10/17.
 */
public class ReflectionUtils {

    private static MethodHandles.Lookup trusted = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {}

    public static Iterable<Field> getAllInstanceFields(Class<?> clazz, boolean inherited, boolean accessible) {
        ArrayList<Field> fields = new ArrayList<>();
        //We do not filter transient because we don't allow to override serialization process like Java.
        Predicate<Field> filter = field -> !Modifier.isStatic(field.getModifiers());

        addAllInstanceFields(clazz, fields, filter, accessible);
        if(inherited)
            addSuperClassFields(clazz, fields, filter, accessible);

        fields.sort(Comparator.comparing(Field::getName));//Ensuring same order for reflector manipulation
        return fields;
    }

    /**
     * Gets a class Field instance for the field name. It can search in the class hierarchy.
     * @param clazz
     * @param name
     * @param inherited
     * @return
     * @throws NoSuchFieldException if no instance is found
     */
    public static Field getField(Class<?> clazz, String name, boolean inherited) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if(Object.class == clazz || !inherited) throw e;
        }
        return getField(clazz.getSuperclass(), name, inherited);
    }

    private static void addAllInstanceFields(Class<?> clazz, List<Field> fields, Predicate<Field> filter, boolean accessible) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if(filter.test(field)){
                field.setAccessible(accessible);
                fields.add(field);
            }
        }
    }

    private static void addSuperClassFields(Class<?> clazz, List<Field> fields, Predicate<Field> filter, boolean accessible) {
        if(Object.class == clazz) return;

        Class curr = clazz.getSuperclass();
        if(Object.class == curr) return;

        addAllInstanceFields(curr, fields, filter, accessible);
        addSuperClassFields(curr, fields, filter, accessible);
    }

    private static MethodHandles.Lookup createTrustedLookup() throws IllegalStateException {
        try {
            final MethodHandles.Lookup temp = MethodHandles.lookup();
            final Field impl_lookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            impl_lookup.setAccessible(true);
            MethodHandles.Lookup toStore = (MethodHandles.Lookup) impl_lookup.get(temp);
            impl_lookup.setAccessible(false);
            return toStore;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Cannot create trusted lookup");
            throw new IllegalStateException(e);
        }
    }

    /**
     *
     * @param clazz
     * @param args this has a limitation, objects passed by varargs cannot be null!
     * @param <T>
     * @return
     * @throws ReflectiveOperationException
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) throws ReflectiveOperationException {
        if(null == args || args.length == 0) return defaultConstructor(clazz);
        Constructor<T> ctr = clazz.getDeclaredConstructor(getTypes(args));
        return ctr.newInstance(args);
    }

    private static Class<?>[] getTypes(Object[] args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return argTypes;
    }

    private static <T> T defaultConstructor(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        return constructor.newInstance();
    }

    public static MethodHandles.Lookup getTrustedLookup() {
        if(null == trusted){
            synchronized (ReflectionUtils.class) {
                if(null == trusted){
                    trusted = createTrustedLookup();
                }
            }
        }
        return trusted;
    }
}
