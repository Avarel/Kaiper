package xyz.avarel.aje.functional;

import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.others.Undefined;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked") // i'm sorry god
public interface NativeFunction extends Any<NativeFunction>, NativeObject<Function<List<Any>, Any>> {
    Type<NativeFunction> TYPE = new Type<>("native function");

    @Override
    default Any invoke(List<Any> args) {
        if (this instanceof _1) {
            return ((_1) this).invoke(args.get(0));
        } else if (this instanceof _2) {
            return ((_2) this).invoke(args.get(0), args.get(1));
        } else if (this instanceof _3) {
            return ((_3) this).invoke(args.get(0), args.get(1), args.get(2));
        } else if (this instanceof _VARARGS) {
            return this.invoke(args);
        }
        return Undefined.VALUE;
    }

    @Override
    default Type<NativeFunction> getType() {
        return TYPE;
    }

    @Override
    default Function<List<Any>, Any> toNative() {
        return this::invoke;
    }

    interface _1<A> extends NativeFunction {
        Any invoke(A a1);
    }

    interface _2<A, B> extends NativeFunction {
        Any invoke(A a1, B a2);
    }

    interface _3<A, B, C> extends NativeFunction {
        Any invoke(A a1, B a2, C a3);
    }

    interface _VARARGS extends NativeFunction {
        @Override
        Any invoke(List<Any> args);
    }
}
