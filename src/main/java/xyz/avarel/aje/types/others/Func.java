package xyz.avarel.aje.types.others;

import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.NativeObject;

import java.util.List;
import java.util.function.Function;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
@FunctionalInterface
public interface Func extends AJEObject<Func>, NativeObject<Function<List<AJEObject>, AJEObject>> {
    AJEType<Func> TYPE = new AJEType<>("function");

    @Override
    default Function<List<AJEObject>, AJEObject> toNative() {
        return this::invoke;
    }

    @Override
    default AJEType<Func> getType() {
        return TYPE;
    }

    @Override
    AJEObject invoke(List<AJEObject> arguments);
}
