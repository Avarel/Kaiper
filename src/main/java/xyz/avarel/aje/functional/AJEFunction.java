package xyz.avarel.aje.functional;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Type;

import java.util.List;
import java.util.function.Function;

public abstract class AJEFunction implements Any<AJEFunction>, NativeObject<Function<List<Any>, Any>> {
    public static final Type<AJEFunction> TYPE = new Type<>("function");

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
}
