package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import javax.annotation.Nullable;

/**
 * Created by Nuno on 03/06/2017.
 */
public class ClassNameIndex implements IIndexExtractor {


    @Override
    public byte[] immutableIndexes(Object instance) throws ManipulatorException {
        return fromClassName(instance.getClass());
    }

    @Override
    @Nullable
    public byte[] mutableIndexes(Object instance) throws ManipulatorException {
        return null;
    }

    public static byte[] fromClassName(Class<?> clazz) {
        return ByteConverter.fromObject(clazz);
    }
}
