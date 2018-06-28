package pt.isel.ncml.objectivedb.reflector.manipulator;

import com.google.common.collect.ImmutableMap;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static pt.isel.ncml.objectivedb.util.reflection.ReflectionUtils.getAllInstanceFields;


/**
 * Created by nuno on 4/1/17.
 */
public class ReflectionFactory implements IFieldManipulatorFactory {

    private final IObjectResolver objectResolver;
    private final ConfigIndexes indexes;

    @Inject
    public ReflectionFactory(IObjectResolver objectResolver, ConfigIndexes indexes) {
        this.objectResolver = objectResolver;
        this.indexes = indexes;
    }

    @Override
    public IFieldStateManipulator getFieldManipulator(Class<?> objClass) throws ManipulatorException {
        Builder builder = new Builder(objectResolver, indexes);
        for (Field field : getAllInstanceFields(objClass, true, true)) {
            builder.add(field);
        }
        return builder.build();
    }

    private interface SpecificBuilderCall {
        void consume(Builder instance, Field field);
    }

    private static class Builder {

        //Could be instance Map but would be an excessive overhead
        //Simulates instance calls, but with no inheritance
        private static final Map<Class<?>, SpecificBuilderCall> primMap = ImmutableMap.<Class<?>, SpecificBuilderCall>builder()
                .put(int.class, Builder::_int)
                .put(long.class, Builder::_long)
                .put(float.class, Builder::_float)
                .put(double.class, Builder::_double)
                .put(byte.class, Builder::_byte)
                .put(char.class, Builder::_char)
                .put(short.class, Builder::_short)
                .put(boolean.class, Builder::_boolean)
                .build();

        private final StateBuilder stateBuilder;

        public Builder(IObjectResolver objectResolver, ConfigIndexes indexes) {
            stateBuilder = new StateBuilder(objectResolver, IndexExtractor.builder(indexes));
        }

        public IFieldStateManipulator build() {
            return new StateManipulator(stateBuilder, stateBuilder);
        }

        public Builder add(Field field) {
            Class<?> fieldType = field.getType();
            //Primitives
            final SpecificBuilderCall specificBuilderCall;
            if((specificBuilderCall = primMap.get(fieldType)) != null){
                specificBuilderCall.consume(this, field);
                return this;
            }
            //Boxed primitives must be considered objects. Check MiscelaneousTest#changeBoxedPrimitiveValue
            _object(this, field);
            return this;
        }

        private static void _int(Builder instance, Field field) {
            instance.stateBuilder.intMod(field);
        }

        private static void _long(Builder instance, Field field) {
            instance.stateBuilder.longMod(field);
        }

        private static void _float(Builder instance, Field field) {
            instance.stateBuilder.floatMod(field);
        }

        private static void _double(Builder instance, Field field) {
            instance.stateBuilder.doubleMod(field);
        }

        private static void _byte(Builder instance, Field field) {
            instance.stateBuilder.byteMod(field);
        }

        private static void _short(Builder instance, Field field) {
            instance.stateBuilder.shortGetter(field);
        }

        private static void _char(Builder instance, Field field) {
            instance.stateBuilder.charMod(field);
        }

        private static void _boolean(Builder instance, Field field) {
            instance.stateBuilder.booleanMod(field);
        }

        private static void _object(Builder instance, Field field) {
            instance.stateBuilder.objectMod(field);
        }

    }

    private static class StateBuilder implements IStateGetter, IStateSetter {

        private final List<IFieldByteGetter> byteSupplier = new ArrayList<>();
        private final List<IFieldStateSetter> setterList = new ArrayList<>();
        private final IObjectResolver objectResolver;
        private IndexExtractor.Builder indexBuilder;
        private Supplier<IIndexExtractor> indexes = this::buildIndexes;

        public StateBuilder(IObjectResolver objectResolver, IndexExtractor.Builder indexBuilder) {
            this.objectResolver = objectResolver;
            this.indexBuilder = indexBuilder;
        }

        private IIndexExtractor buildIndexes() {
            IIndexExtractor idxTemp = indexBuilder.build();
            this.indexBuilder = null;
            this.indexes = () -> idxTemp;
            return idxTemp;
        }

        @Override
        public IStateInfo getStateInfo(Object t) {
            return new DelayedStateInfo(t, byteSupplier.iterator(), indexes.get());
        }

        @Override
        public void setStateInfo(Object instance, IStateInfo info) throws ManipulatorException {
            for (IFieldStateSetter setter : setterList) {
                setter.set(instance, info);
            }
        }

        private StateBuilder intMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromInt(field.getInt(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setInt(obj, stateInfo.getInt());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder floatMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromFloat(field.getFloat(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setFloat(obj, stateInfo.getFloat());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder doubleMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromDouble(field.getDouble(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setDouble(obj, stateInfo.getDouble());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder byteMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromByte(field.getByte(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setByte(obj, stateInfo.getByte());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder charMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromChar(field.getChar(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setChar(obj, stateInfo.getChar());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder shortGetter(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromShort(field.getShort(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setShort(obj, stateInfo.getShort());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder longMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromLong(field.getLong(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setLong(obj, stateInfo.getLong());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        private StateBuilder booleanMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromBoolean(field.getBoolean(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.setBoolean(obj, stateInfo.getBoolean());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }

        public StateBuilder objectMod(Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return objectResolver.toByteArray(field.get(instance));
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    field.set(obj, stateInfo.getObject());
                } catch (IllegalAccessException e) {
                    throw new ManipulatorException(e);
                }
            });
            return this;
        }
    }

}
