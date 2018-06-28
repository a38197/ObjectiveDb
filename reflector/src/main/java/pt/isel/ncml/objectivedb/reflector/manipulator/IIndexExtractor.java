package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

import javax.annotation.Nullable;

/**
 * Created by Nuno on 03/06/2017.
 */
public interface IIndexExtractor {
    /**
     * Converts the instance state mutable indexes to bytes.
     * The class name is viewed as an immutable fixed index therefore at least one will exist
     * @param instance
     * @return
     * @throws ManipulatorException
     */
    byte[] immutableIndexes(Object instance) throws ManipulatorException;

    /**
     * Converts the instance state mutable indexes to bytes.
     * @param instance
     * @return null if no indexes are configured
     * @throws ManipulatorException
     */
    @Nullable
    byte[] mutableIndexes(Object instance) throws ManipulatorException;
}
