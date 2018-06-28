package pt.isel.ncml.objectivedb.reflector.manipulator;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * Created by ncaro on 3/30/2017.
 */
public class PerformanceTest {
    private static class IntField{
        private int i1;

        public IntField(int i1) {
            this.i1 = i1;
        }
    }

    private static class PIntField{
        public int i1;

        public PIntField(int i1) {
            this.i1 = i1;
        }
    }

    @Test
    public void test() throws Throwable {
        final IntField intData = new IntField(3);
        final PIntField pIntData = new PIntField(3);
        final Field intField = IntField.class.getDeclaredField("i1");
        intField.setAccessible(true);//Faz a refleçao bem mais rapida
        final Field pintField = PIntField.class.getDeclaredField("i1");
        final MethodHandle unreflectSetInt = MethodHandles.lookup().unreflectSetter(intField);
        final MethodHandle unreflectGetInt = MethodHandles.lookup().unreflectGetter(intField);
        final MethodHandle unreflectSetPInt = MethodHandles.lookup().unreflectSetter(pintField);
        final MethodHandle unreflectGetPInt = MethodHandles.lookup().unreflectGetter(pintField);
//        final MethodHandle inlineSetInt = MethodHandles.lookup().findSetter(IntField.class, "i1", int.class);
//        final MethodHandle inlineGetInt = MethodHandles.lookup().findGetter(IntField.class, "i1", int.class);
        final MethodHandle inlineSetPInt = MethodHandles.lookup().findSetter(PIntField.class, "i1", int.class);
        final MethodHandle inlineGetPInt = MethodHandles.lookup().findGetter(PIntField.class, "i1", int.class);


        final int count = 100000000;
        doLogged("set reflection private with set accessible", ()->{
            for (int i = 0; i < count; i++) {
                intField.setInt(intData, i);
            }
        });

        doLogged("set unreflect private with set accessible", ()->{
            for (int i = 0; i < count; i++) {
                unreflectSetInt.invokeExact(intData, i);
            }
        });

        doLogged("set reflection public", ()->{
            for (int i = 0; i < count; i++) {
                pintField.setInt(pIntData, i);
            }
        });

        doLogged("set unreflect public", ()->{
            for (int i = 0; i < count; i++) {
                unreflectSetPInt.invokeExact(pIntData, i);
            }
        });

        doLogged("inline set handles public", ()->{
            for (int i = 0; i < count; i++) {
                inlineSetPInt.invokeExact(pIntData, i);
            }
        });

        doLogged("get reflection private with set accessible", ()->{
            for (int i = 0; i < count; i++) {
                intField.getInt(intData);
            }
        });

        doLogged("get unreflect private with set accessible", ()->{
            for (int i = 0; i < count; i++) {
                int temp = (int)unreflectGetInt.invokeExact(intData);
            }
        });

        doLogged("get reflection public", ()->{
            for (int i = 0; i < count; i++) {
                pintField.getInt(pIntData);
            }
        });

        doLogged("get unreflect public", ()->{
            for (int i = 0; i < count; i++) {
                int temp = (int)unreflectGetPInt.invokeExact(pIntData);
            }
        });

        doLogged("inline get handles public", ()->{
            for (int i = 0; i < count; i++) {
                int temp = (int)inlineGetPInt.invokeExact(pIntData);
            }
        });
    }

    interface DoWithException<EX extends Throwable>{
        void dwe() throws EX;
    }

    private static void doLogged(String name, DoWithException<Throwable> runnable) {
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


    @Test
    public void trustedLookup() throws Throwable {
        // Define black magic.
        final Lookup original = MethodHandles.lookup();
        final Field internal = Lookup.class.getDeclaredField("IMPL_LOOKUP");
        internal.setAccessible(true);
        final Lookup trusted = (Lookup) internal.get(original);
//        internal.setAccessible(false);

        final Lookup caller = trusted.in(IntField.class);
        final MethodHandle setter = caller.findSetter(IntField.class, "i1", int.class);
        final MethodHandle getter = caller.findGetter(IntField.class, "i1", int.class);
        final IntField intField = new IntField(5);

        final int count = 100000000;
        doLogged("trusted handle private setter", ()->{
            for (int i = 0; i < count; i++) {
                setter.invokeExact(intField, i);
            }
        });

        doLogged("trusted handle private getter", ()->{
            for (int i = 0; i < count; i++) {
                int tmp = (int)getter.invokeExact(intField);
            }
        });
    }

    @Test
    public void varHandle() throws Exception {

    }
}
