package pt.isel.ncml.objectivedb.reflector.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Created by nuno on 4/24/17.
 */
public class ManipulatorException extends ReflectorException {

    public ManipulatorException(@NotNull Throwable cause) {
        super(cause);
    }

    public ManipulatorException(@NotNull String msg) {
        super(msg);
    }

    public ManipulatorException(@NotNull String msg, @NotNull Throwable cause) {
        super(msg, cause);
    }
}
