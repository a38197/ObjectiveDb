package pt.isel.ncml.objectivedb.reflector;

import com.google.common.collect.FluentIterable;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;

/**
 * Recursive reflective task for Object field discovery.
 */
class ObjectReflectTask extends BaseReflectTask {
    public ObjectReflectTask(Object obj, Class<?> aClass, IMetaStore metaStore) {
        super(obj, aClass, metaStore);
    }

    @Override
    public IFieldStateManipulator fieldManipulator() {
        return getMetaStore().getFieldManipulator(getReflectedClass());
    }

    @Override
    public Iterable<IReflectedObject> innerReflectedObjects() {
        return FluentIterable
                .from(getMetaStore().getClassInspector(getReflectedClass()).getObjectReferences(getObject()))
                .transform(this::innerReflect);
    }

}
