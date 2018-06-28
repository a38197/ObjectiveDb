package pt.isel.ncml.objectivedb.reflector.inspector;

import com.google.common.collect.FluentIterable;
import kotlin.jvm.functions.Function0;
import pt.isel.ncml.objectivedb.exception.DbError;
import pt.isel.ncml.objectivedb.util.FunctionsKt;
import pt.isel.ncml.objectivedb.util.reflection.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Caches field data.
 */
public class ClassInspector implements IClassInspector {

    private final Class<?> tClass;
    private final Function0<FluentIterable<Field>> memorizedFields;

    public ClassInspector(Class<?> tClass) {
        this.tClass = tClass;
        memorizedFields = FunctionsKt.memorizeSync(() -> FluentIterable.from(ReflectionUtils.getAllInstanceFields(tClass, true, true)));
    }

    @Override
    public Iterable<Object> getObjectReferences(Object obj) {
        return memorizedFields.invoke()
                .filter(input -> !input.getType().isPrimitive())
                .transform(input -> {
                    try {
                        return input.get(obj);
                    } catch (IllegalAccessException e) {
                        throw new DbError("Cannot get access to object references",e);
                    }
                });
    }

    @Override
    public Class<?> getInspectedClass() {
        return tClass;
    }
}
