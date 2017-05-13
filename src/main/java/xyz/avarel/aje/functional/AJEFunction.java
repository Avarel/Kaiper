package xyz.avarel.aje.functional;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Type;

import java.util.List;

public abstract class AJEFunction implements Any<AJEFunction> {
    public static final Type TYPE = new Type("function");

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public abstract Any invoke(List<Any> arguments);
}
