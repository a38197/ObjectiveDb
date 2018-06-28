package pt.isel.ncml.objectivedb.reflector;

import org.junit.Ignore;
import org.junit.Test;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.manipulator.IObjectResolver;

/**
 * Created by Mario on 2017-05-03.
 */
public class ObjectReflectTaskTest {


    @Test
    @Ignore
    public void testObjectReflectorTask(){
        final Batata batata = new Batata();
        final Cebola cebola = new Cebola();
        batata.cebola = cebola;
        cebola.batata = batata;
        final ReflectionMetaStorage reflectionMetaStorage = new ReflectionMetaStorage(new BananaResolver(),new ConfigIndexes());
        final ObjectReflectTask objectReflectTask = new ObjectReflectTask(batata, Batata.class, reflectionMetaStorage);
        objectReflectTask.innerReflectedObjects()
                .forEach(reflectedObject -> System.out.println(reflectedObject.getReflectedClass().getCanonicalName()));
    }

    class Batata{
        Cebola cebola;
    }

    class Cebola{
        Batata batata;
    }

    class BananaResolver implements IObjectResolver{

        @Override
        public byte[] toByteArray(Object obj) {
            if(obj instanceof Batata){
                return "batatas".getBytes();
            }else{
                return "cebolas".getBytes();
            }
        }

        @Override
        public int getReferenceSize() {
            throw new UnsupportedOperationException();
        }
    }

}