package pt.isel.ncml.objectivedb.reflector.inspector;

/**
 * Created by nuno on 4/23/17.
 */
public interface IClassInspector {

    /**
     * @param obj the instance to extract the inner objects
     * @return the objects that the input object can reference
     */
    Iterable<Object> getObjectReferences(Object obj);

    /**
     * @return the class that this inspector can handle
     */
    Class<?> getInspectedClass();

    interface IClassInspectorFunction {
        IClassInspector get(Class<?> cls);
    }
}
