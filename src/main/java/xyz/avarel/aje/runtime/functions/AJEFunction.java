package xyz.avarel.aje.runtime.functions;

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
    public abstract Any invoke(List<Any> args);

    @Override
    public Any plus(Any other) {
        if (other instanceof AJEFunction) {
            return plus((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction plus(AJEFunction right) {
        return new CombinedFunction(this, right, Any::plus);
    }

    @Override
    public Any minus(Any other) {
        if (other instanceof AJEFunction) {
            return minus((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction minus(AJEFunction right) {
        return new CombinedFunction(this, right, Any::minus);
    }

    @Override
    public Any times(Any other) {
        if (other instanceof AJEFunction) {
            return times((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction times(AJEFunction right) {
        return new CombinedFunction(this, right, Any::times);
    }

    @Override
    public Any divide(Any other) {
        if (other instanceof AJEFunction) {
            return divide((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction divide(AJEFunction right) {
        return new CombinedFunction(this, right, Any::divide);
    }
}
