package xyz.avarel.aje.runtime.functional;

import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.NativeObject;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;
import java.util.function.Function;

public abstract class AJEFunction implements Any, NativeObject<Function<List<Any>, Any>> {
    public static final Type<AJEFunction> TYPE = new Type<>("function");

    public abstract int getArity();

    @Override
    public Type<AJEFunction> getType() {
        return TYPE;
    }

    @Override
    public Function<List<Any>, Any> toNative() {
        return this::invoke;
    }

    @Override
    public abstract Any invoke(List<Any> arguments);

    @Override
    public Any plus(Any other) {
        if (other instanceof AJEFunction) {
            return plus((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction plus(AJEFunction right) {
        AJEFunction left = this;

        return new AJEFunction() {
            @Override
            public int getArity() {
                return 1;
            }

            @Override
            public Any invoke(List<Any> arguments) {
                if (arguments.size() == getArity()) {
                    return left.invoke(arguments).plus(right.invoke(arguments));
                }
                return Undefined.VALUE;
            }
        };
    }

    @Override
    public Any minus(Any other) {
        if (other instanceof AJEFunction) {
            return minus((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction minus(AJEFunction right) {
        AJEFunction left = this;

        return new AJEFunction() {
            @Override
            public int getArity() {
                return 1;
            }

            @Override
            public Any invoke(List<Any> arguments) {
                if (arguments.size() == getArity()) {
                    return left.invoke(arguments).minus(right.invoke(arguments));
                }
                return Undefined.VALUE;
            }
        };
    }

    @Override
    public Any times(Any other) {
        if (other instanceof AJEFunction) {
            return times((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction times(AJEFunction right) {
        AJEFunction left = this;

        return new AJEFunction() {
            @Override
            public int getArity() {
                return 1;
            }

            @Override
            public Any invoke(List<Any> arguments) {
                if (arguments.size() == getArity()) {
                    return left.invoke(arguments).times(right.invoke(arguments));
                }
                return Undefined.VALUE;
            }
        };
    }

    @Override
    public Any divide(Any other) {
        if (other instanceof AJEFunction) {
            return divide((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction divide(AJEFunction right) {
        AJEFunction left = this;

        return new AJEFunction() {
            @Override
            public int getArity() {
                return 1;
            }

            @Override
            public Any invoke(List<Any> arguments) {
                if (arguments.size() == getArity()) {
                    return left.invoke(arguments).divide(right.invoke(arguments));
                }
                return Undefined.VALUE;
            }
        };
    }
}
