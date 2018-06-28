package pt.isel.ncml.objectivedb.reflector;

import pt.isel.ncml.objectivedb.reflector.inspector.IClassInspector;
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator;

import javax.annotation.Nonnull;

/**
 * Interface for metadata sharing.
 * <p>Implementations must have a constructor with {@link pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver} parameter.
 * This parameter will be provided by a different module.</p>
 */
public interface IMetaStore {

    /**
     * <p>The property key that must have a class name of the implementation to use.</p>
     * <p>Available values are {@link MethodHandlesMetaStorage}, {@link ReflectionMetaStorage}</p>
     */
    String META_STORE_IMPL = "metastore.implementation";

    /**
     * Gets a field manipulator for the given class.
     * Depending on the implementation, the get process may have to generate a {@link IFieldStateManipulator}. This may be done using locks.
     * It's not guaranteed that the same manipulator is returned, to improve memory management.
     * @param aClass the class to manipulate state
     * @return a valid, non null, {@link IFieldStateManipulator}
     * @throws IllegalArgumentException if the class is not supported. Arrays have specific manipulators
     */
    @Nonnull
    IFieldStateManipulator getFieldManipulator(Class<?> aClass) throws IllegalArgumentException;

    /**
     * Gets a manipulator for arrays with the same constraints as the {@linkplain #getFieldManipulator(Class)}.
     * It's expected that a limited number of manipulators exist, one for each primitive and one for object references.
     * @param componentType the class to manipulate state
     * @return a valid, non null, {@link IFieldStateManipulator}
     * @throws IllegalArgumentException if the class is not supported. All non array classes are not supported
     */
    @Nonnull
    IFieldStateManipulator getArrayManipulator(Class<?> componentType) throws IllegalArgumentException;

    /**
     * Gets a {@link IClassInspector} for the given class with the same constraints as the {@linkplain #getFieldManipulator(Class)}.
     * @param aClass class to inspect
     * @return a valid, non null, {@link IClassInspector}
     * @throws IllegalArgumentException if the class does not supports an inspector
     */
    @Nonnull
    IClassInspector getClassInspector(Class<?> aClass) throws IllegalArgumentException;
}
