package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Nuno on 13/06/2017.
 */
final class ManipulatorUtils {
    private ManipulatorUtils() {}

    static byte[] asBytes(Iterator<IFieldByteGetter> source, Object instance) throws ManipulatorException {
        try {
            //This implementation performs better than many other approaches, even being synchronized.
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (source.hasNext()){
                outputStream.write(source.next().get(instance));
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ManipulatorException(e);
        }
    }
}
