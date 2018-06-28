package pt.isel.ncml.objectivedb.reflector.exception;

import org.jetbrains.annotations.NotNull;
import pt.isel.ncml.objectivedb.exception.DbException;

/**
 * Created by nuno on 4/24/17.
 */
public class ReflectorException extends DbException {

    public ReflectorException(@NotNull Throwable cause) {
        super(cause);
    }

    public ReflectorException(@NotNull String msg) {
        super(msg);
    }

    public ReflectorException(@NotNull String msg, @NotNull Throwable cause) {
        super(msg, cause);
    }

}
