package pt.isel.ncml.objectivedb.reflector.manipulator;

import com.google.common.collect.ImmutableMap;
import pt.isel.ncml.objectivedb.index.ConfigIndexes;
import pt.isel.ncml.objectivedb.reflector.exception.ManipulatorException;
import pt.isel.ncml.objectivedb.util.ByteConverter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static pt.isel.ncml.objectivedb.util.reflection.ReflectionUtils.getAllInstanceFields;

/**
 * Created by ncaro on 3/29/2017.
 */
public class MethodHandleFactory implements IFieldManipulatorFactory {

    private static final Lookup lookup = MethodHandles.lookup();

    private final IObjectResolver resolver;
    private final ConfigIndexes indexes;

    public MethodHandleFactory(IObjectResolver resolver, ConfigIndexes indexes) {
        this.resolver = resolver;
        this.indexes = indexes;
    }

    @Override
    public IFieldStateManipulator getFieldManipulator(Class<?> objClass) throws ManipulatorException {
        Builder builder = new Builder(resolver, indexes);

        try {
            for (Field field : getAllInstanceFields(objClass, true, true)) {
                final MethodHandle setter = lookup.unreflectSetter(field);
                final MethodHandle getter = lookup.unreflectGetter(field);
                builder.add(getter, setter, field);
            }
        } catch (IllegalAccessException e) {
            throw new ManipulatorException(e);
        }

        return builder.build();
    }

    private interface SpecificBuilderCall {
        void consume(Builder instance, MethodHandle getter, MethodHandle setter, Field field);
    }

    private static class Builder {

        private static final Map<Class, SpecificBuilderCall> primMap = ImmutableMap.<Class, SpecificBuilderCall>builder()
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

        public Builder add(MethodHandle setter, MethodHandle getter, Field field) {
            //Primitives
            final SpecificBuilderCall specificBuilderCall;
            if((specificBuilderCall = primMap.get(field.getType())) != null){
                specificBuilderCall.consume(this, setter, getter, field);
                return this;
            }
            //Boxed primitives must be considered objects. Check MiscelaneousTest#changeBoxedPrimitiveValue
            //Objects (array references are objects)
            _object(this, setter, getter, field);
            return this;
        }

        private static void _int(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.intMod(getter, setter, field);
        }

        private static void _long(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.longMod(getter, setter, field);
        }

        private static void _float(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.floatMod(getter, setter, field);
        }

        private static void _double(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.doubleMod(getter, setter, field);
        }

        private static void _byte(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.byteMod(getter, setter, field);
        }

        private static void _short(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.shortMod(getter, setter, field);
        }

        private static void _char(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.charMod(getter, setter, field);
        }

        private static void _boolean(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.booleanMod(getter, setter, field);
        }

        private static void _object(Builder instance, MethodHandle getter, MethodHandle setter, Field field) {
            instance.stateBuilder.objectMod(getter, setter, field);
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

        private void intMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromInt((int) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getInt());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        private void floatMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromFloat((float) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getFloat());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        private void doubleMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromDouble((double) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getDouble());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        private void byteMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromByte((byte) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getByte());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        private void charMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromChar((char) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getChar());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        private void shortMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromShort((short) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getShort());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        private void longMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromLong((long) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getLong());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        public void booleanMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return ByteConverter.fromBoolean((boolean) getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            },field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getBoolean());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }

        public void objectMod(MethodHandle getter, MethodHandle setter, Field field) {
            byteSupplier.add(indexBuilder.add((instance) -> {
                try {
                    return objectResolver.toByteArray(getter.invoke(instance));
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            }, field));
            setterList.add((obj, stateInfo) -> {
                try {
                    setter.invoke(obj, stateInfo.getObject());
                } catch (Throwable throwable) {
                    throw new ManipulatorException(throwable);
                }
            });
        }
    }

}
