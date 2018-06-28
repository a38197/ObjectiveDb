package pt.isel.ncml.objectivedb.reflector;

/**
 * Base object reflect task
 */
abstract class BaseReflectTask implements IReflectedObject {

    private final Object obj;
    private final Class<?> aClass;
    private final IMetaStore metaStore;

    protected BaseReflectTask(Object obj, Class<?> aClass, IMetaStore metaStore) {
        this.obj = obj;
        this.aClass = aClass;
        this.metaStore = metaStore;
    }

    @Override
    public Class<?> getReflectedClass() {
        return aClass;
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public Object getObject() {
        return obj;
    }

    protected final IMetaStore getMetaStore() {
        return metaStore;
    }

    protected final IReflectedObject innerReflect(Object innerObj) {
        if(null == innerObj){
            return IReflectedObject.NULL;
        }

        final Class<?> innerClass = innerObj.getClass();
        if(innerClass.getComponentType() != null){
            return new ArrayReflectTask(innerObj, innerClass, innerClass.getComponentType(), metaStore);
        }
        return new ObjectReflectTask(innerObj, innerClass, metaStore);
    }
}
