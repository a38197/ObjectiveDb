package pt.isel.ncml.objectivedb.reflector;

import com.google.common.collect.FluentIterable;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;

import javax.annotation.Nonnull;
import java.util.Collections;

class ArrayReflectTask extends BaseReflectTask {

    @Nonnull
    private final Class<?> componentType;

    /**
     * Constructs and array state manipulator for the given component type
     * @param obj array instance
     * @param componentType array component type
     * @param metaStore array manipulator for component type
     */
    public ArrayReflectTask(@Nonnull Object obj, Class<?> objClass, @Nonnull Class<?> componentType, @Nonnull IMetaStore metaStore) {
        super(obj, objClass, metaStore);
        this.componentType = componentType;
    }

    @Override
    public IFieldStateManipulator fieldManipulator() {
        return getMetaStore().getArrayManipulator(componentType);
    }

    @Override
    public Iterable<IReflectedObject> innerReflectedObjects() {
        if(componentType.isPrimitive()){
            return Collections.emptyList();
        }

        return FluentIterable
                .of((Object[]) getObject())
                .transform(this::innerReflect);
    }

}
