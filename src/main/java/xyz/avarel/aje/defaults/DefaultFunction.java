package xyz.avarel.aje.defaults;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.functional.NFunction;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.Value;
import xyz.avarel.aje.types.numbers.Decimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum DefaultFunction implements NFunction {
    SIN(Decimal.TYPE, (arg) -> Decimal.of(Math.sin(arg.toNative()))),
    COS(Decimal.TYPE, (arg) -> Decimal.of(Math.cos(arg.toNative()))),
    TAN(Decimal.TYPE, (arg) -> Decimal.of(Math.tan(arg.toNative()))),
    ASIN(Decimal.TYPE, (arg) -> Decimal.of(Math.asin(arg.toNative()))),
    ACOS(Decimal.TYPE, (arg) -> Decimal.of(Math.acos(arg.toNative()))),
    ATAN(Decimal.TYPE, (arg) -> Decimal.of(Math.atan(arg.toNative()))),
    ATAN2(Decimal.TYPE, Decimal.TYPE, (a1, a2) -> Decimal.of(Math.atan2(a1.toNative(), a2.toNative()))),
    ;

    private final NFunction function;
    private List<AJEType> arguments;

    <AT extends AJEType<A>, A>
    DefaultFunction(AT type1, NFunction._1<A> function) {
        arguments = Collections.singletonList(type1);
        this.function = function;
    }

    <AT extends AJEType<A>, A, BT extends AJEType<B>, B>
    DefaultFunction(AT type1, BT type2, NFunction._2<A, B> function) {
        arguments = Arrays.asList(type1, type2);
        this.function = function;
    }

    <AT extends AJEType<A>, A, BT extends AJEType<B>, B, CT extends AJEType<C>, C>
    DefaultFunction(AT type1, BT type2, CT type3, NFunction._3<A, B, C> function) {
        arguments = Arrays.asList(type1, type2, type3);
        this.function = function;
    }

    @Override
    public AJEObject invoke(List<AJEObject> args) {
        if (args.size() != arguments.size()) {
            throw new AJEException("Function " + this + " needs " + arguments.size() + " arguments. Got " + args.size() + ".");
        }

        List<AJEObject> _args = new ArrayList<>();
        try {
            for (int i = 0; i < arguments.size(); i++) {
                _args.add(args.get(i).castTo(arguments.get(i)));
            }
        } catch (AJEException e) {
            String invokingTypes = args.stream().map(Value::getType).map(Object::toString).collect(Collectors.joining(", ", "(", ")"));
            String requiredTypes = arguments.stream().map(Object::toString).collect(Collectors.joining(", ", "(", ")"));
            throw new AJEException(this + requiredTypes + " can not be applied to " + invokingTypes);
        }

        return function.invoke(_args);
    }
}
