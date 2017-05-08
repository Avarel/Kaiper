package xyz.avarel.aje.functional;

import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.others.Nothing;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked") // i'm sorry god
public interface NFunction extends AJEObject<NFunction>, NativeObject<Function<List<AJEObject>, AJEObject>> {
    AJEType<NFunction> TYPE = new AJEType<>("function");

    @Override
    default AJEObject invoke(List<AJEObject> args) {
        if (this instanceof _1) {
            return ((_1) this).invoke(args.get(0));
        } else if (this instanceof _2) {
            return ((_2) this).invoke(args.get(0), args.get(1));
        } else if (this instanceof _3) {
            return ((_3) this).invoke(args.get(0), args.get(1), args.get(2));
        }
        return Nothing.VALUE;
    }

    @Override
    default AJEType<NFunction> getType() {
        return TYPE;
    }

    @Override
    default Function<List<AJEObject>, AJEObject> toNative() {
        return this::invoke;
    }

    interface _1<A> extends NFunction {
        AJEObject invoke(A a1);
    }

    interface _2<A, B> extends NFunction {
        AJEObject invoke(A a1, B a2);
    }

    interface _3<A, B, C> extends NFunction {
        AJEObject invoke(A a1, B a2, C a3);
    }

    interface _N extends NFunction {
        @Override
        AJEObject invoke(List<AJEObject> args);
    }
}
