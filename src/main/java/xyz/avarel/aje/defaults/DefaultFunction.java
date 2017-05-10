package xyz.avarel.aje.defaults;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.functional.NativeFunction;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Undefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DefaultFunction implements NativeFunction {
    SIN(Decimal.TYPE, (arg) -> Decimal.of(Math.sin(arg.toNative()))),
    COS(Decimal.TYPE, (arg) -> Decimal.of(Math.cos(arg.toNative()))),
    TAN(Decimal.TYPE, (arg) -> Decimal.of(Math.tan(arg.toNative()))),
    ASIN(Decimal.TYPE, (arg) -> Decimal.of(Math.asin(arg.toNative()))),
    ACOS(Decimal.TYPE, (arg) -> Decimal.of(Math.acos(arg.toNative()))),
    ATAN(Decimal.TYPE, (arg) -> Decimal.of(Math.atan(arg.toNative()))),
    ATAN2(Decimal.TYPE, Decimal.TYPE, (a1, a2) -> Decimal.of(Math.atan2(a1.toNative(), a2.toNative()))),

    MAX(Decimal.TYPE, Decimal.TYPE, (a1, a2) -> Decimal.of(Math.max(a1.toNative(), a2.toNative()))),
    MIN(Decimal.TYPE, Decimal.TYPE, (a1, a2) -> Decimal.of(Math.min(a1.toNative(), a2.toNative()))),

    MAP(Slice.TYPE, AJEFunction.TYPE, (a1, a2) -> {
        Slice slice = new Slice();
        for (Any obj : a1) {
            slice.add(a2.invoke(Collections.singletonList(obj)));
        }
        return slice;
    }),
    ;

    private final NativeFunction function;
    private List<Type> parameters;

    <AT extends Type<A>, AN extends NativeObject<A>, A extends Any>
    DefaultFunction(AT type1, NativeFunction._1<A> function) {
        parameters = Collections.singletonList(type1);
        this.function = function;
    }

    <AT extends Type<A>, A extends Any,
            BT extends Type<B>, B extends Any>
    DefaultFunction(AT type1, BT type2, NativeFunction._2<A, B> function) {
        parameters = Arrays.asList(type1, type2);
        this.function = function;
    }

    <AT extends Type<A>, A extends Any,
            BT extends Type<B>, B extends Any,
            CT extends Type<C>, C extends Any>
    DefaultFunction(AT type1, BT type2, CT type3, NativeFunction._3<A, B, C> function) {
        parameters = Arrays.asList(type1, type2, type3);
        this.function = function;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public Any invoke(List<Any> args) {
        if (args.size() < parameters.size()) {
            return Undefined.VALUE;
        }

        List<Any> _args = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            _args.add(args.get(i).castUp(parameters.get(i)));
        }

        for (int i = 0; i < parameters.size(); i++) {
            if (_args.get(i).getType() != parameters.get(i) &&
                    !parameters.get(i).getPrototype().getClass().isInstance(_args.get(i).getType())) {
                return Undefined.VALUE;
            }
        }

        return function.invoke(_args);
    }
}
