package xyz.avarel.aje.functional;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Type;

import java.util.List;

public interface AJEFunction<T extends Any> extends Any<T> {
    Type<AJEFunction> TYPE = new Type<>("function");

    @Override
    default Type<T> getType() {
        return (Type<T>) TYPE;
    }

    @Override
    Any invoke(List<Any> arguments);
}
