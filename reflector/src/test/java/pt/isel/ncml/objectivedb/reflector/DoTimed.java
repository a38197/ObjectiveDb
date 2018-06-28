package pt.isel.ncml.objectivedb.reflector;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Created by nuno on 4/1/17.
 */
public class DoTimed {

    public interface DoWithException<EX extends Throwable>{
        void dwe() throws EX;
    }

    public static void run(String name, DoWithException<Throwable> runnable) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            runnable.dwe();
            final TimeUnit tu = TimeUnit.MILLISECONDS;
            final long elapsed = stopwatch.stop().elapsed(tu);
            System.out.println((elapsed) + "\t "+tu+" -> " + name);
        } catch (Throwable throwable) {
            System.out.println("Test failed " + name + ":" + throwable.getMessage());
        }
    }

}
