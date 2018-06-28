package pt.isel.ncml.objectivedb.reflector;

import com.google.common.base.Preconditions;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;

import javax.annotation.Nonnull;
import javax.inject.Inject;

class ObjectReflector implements IReflector {

    private final IMetaStore metaStore;

    @Inject
    public ObjectReflector(IMetaStore metaStore) {
        this.metaStore = metaStore;
    }

    @Override
    public IFieldStateManipulator getFieldManipulator(Class objClass) throws ManipulatorException {
        if(isArray(objClass)){
            return metaStore.getArrayManipulator(objClass.getComponentType());
        }
        return metaStore.getFieldManipulator(objClass);
    }

    @Override
    public IReflectedObject reflect(@Nonnull Object obj) {
        Preconditions.checkNotNull(obj);

        final Class<?> aClass = obj.getClass();
        if(isArray(aClass)){
            return new ArrayReflectTask(obj, aClass, aClass.getComponentType(), metaStore);
        }
        return new ObjectReflectTask(obj, aClass, metaStore);
    }

    private boolean isArray(Class<?> clazz){
        return clazz.getComponentType() != null;
    }

}
