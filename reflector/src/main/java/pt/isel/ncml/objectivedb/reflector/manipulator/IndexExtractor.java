package pt.isel.ncml.objectivedb.reflector.manipulator;

import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.index.IndexDefinition;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class to create a reusable index extractor
 */
class IndexExtractor {

    private static class OnlyImmutableIndexes implements IIndexExtractor {

        private final Iterable<IFieldByteGetter> immutable;

        private OnlyImmutableIndexes(Iterable<IFieldByteGetter> immutable) {
            this.immutable = immutable;
        }

        @Override
        public byte[] immutableIndexes(Object instance) throws ManipulatorException {
            return ManipulatorUtils.asBytes(immutable.iterator(), instance);
        }

        @Nullable
        @Override
        public byte[] mutableIndexes(Object instance) throws ManipulatorException {
            return null;
        }
    }

    private static class AllIndexes extends OnlyImmutableIndexes {

        private final Iterable<IFieldByteGetter> mutable;

        private AllIndexes(Iterable<IFieldByteGetter> immutable, Iterable<IFieldByteGetter> mutable) {
            super(immutable);
            this.mutable = mutable;
        }

        @Nullable
        @Override
        public byte[] mutableIndexes(Object instance) throws ManipulatorException {
            return ManipulatorUtils.asBytes(mutable.iterator(), instance);
        }
    }

    static Builder builder(ConfigIndexes indexes) {
        return new Builder(indexes);
    }

    static class Builder {
        private final List<IFieldByteGetter> mutable = new ArrayList<>(), immutable = new ArrayList<>();
        private final Set<String> fieldNames = new HashSet<>();//its not memory much efficient but is for temp processing only
        private final ConfigIndexes indexes;

        private Builder(ConfigIndexes indexes) {
            this.indexes = indexes;
            //ClassName is mandatory
            immutable.add(instance -> ByteConverter.fromObject(instance.getClass()));
        }

        IFieldByteGetter add(IFieldByteGetter getter, Field field){
            if(fieldNames.contains(field.getName()))
                return getter;

            IndexDefinition def;
            if((def = getDefinition(field.getDeclaringClass().getName(), field.getName())) == null)
                return getter;

            fieldNames.add(field.getName());
            if(def.getMutable())
                mutable.add(getter);
            else
                immutable.add(getter);

            return getter;
        }

        @Nullable
        private IndexDefinition getDefinition(String className, String fieldName) {
            for (IndexDefinition definition : indexes.get(className)) {
                if(definition.getFieldName().equals(fieldName))
                    return definition;
            }
            return null;
        }

        IIndexExtractor build() {
            return mutable.size() == 0 ?
                    new OnlyImmutableIndexes(immutable) :
                    new AllIndexes(immutable, mutable);
        }
    }
}
