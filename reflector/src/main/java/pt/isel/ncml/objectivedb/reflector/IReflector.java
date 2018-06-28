package pt.isel.ncml.objectivedb.reflector;

import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldManipulatorFactory;

/**
 * Created by ncaro on 3/29/2017.
 */
public interface IReflector extends IFieldManipulatorFactory {

    /**
     * Base for the object graph with reflected properties
     * @param obj
     * @return
     */
    default IReflectedObject reflect(Object obj){throw new UnsupportedOperationException();}

}
