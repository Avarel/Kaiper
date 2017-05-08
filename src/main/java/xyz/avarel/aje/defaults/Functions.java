package xyz.avarel.aje.defaults;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.Value;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.others.Func;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Functions implements Func {
    SIN(Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.sin(((Decimal) args.get(0)).toNative()));
        }
    },
    COS(Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.sin(((Decimal) args.get(0)).toNative()));
        }
    },
    TAN(Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.tan(((Decimal) args.get(0)).toNative()));
        }
    },
    ASIN(Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.asin(((Decimal) args.get(0)).toNative()));
        }
    },
    ACOS(Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.acos(((Decimal) args.get(0)).toNative()));
        }
    },
    ATAN(Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.atan(((Decimal) args.get(0)).toNative()));
        }
    },
    ATAN2(Decimal.TYPE, Decimal.TYPE) {
        @Override
        AJEObject invokeNative(List<AJEObject> args) {
            return Decimal.of(Math.atan2(
                    ((Decimal) args.get(0)).toNative(),
                    ((Decimal) args.get(1)).toNative()));
        }
    },
    ;


    private List<AJEType> arguments;

    Functions(AJEType... arguments) {
        this.arguments = Arrays.asList(arguments);
    }

    public List<AJEType> getArguments() {
        return arguments;
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

        return invokeNative(_args);
    }

    abstract AJEObject invokeNative(List<AJEObject> arguments);
}
