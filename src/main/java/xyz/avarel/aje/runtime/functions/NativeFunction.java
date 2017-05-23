package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class NativeFunction extends AJEFunction {
    private final List<Type> parameters;
    private final boolean varargs;

    public NativeFunction(Type... parameters) {
        this.parameters = Arrays.asList(parameters);
        this.varargs = false;
    }

    public NativeFunction(boolean varargs, Type parameter) {
        this.parameters = Collections.singletonList(parameter);
        this.varargs = varargs;
    }

    @Override
    public String toString() {
        return "native-function";
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    @Override
    public Obj invoke(List<Obj> args) {
        if (!varargs && args.size() != getArity()) {
            return Undefined.VALUE;
        }

        if (!varargs) {
            for (int i = 0; i < parameters.size(); i++) {
                if (!args.get(i).getType().is(parameters.get(i))) {
                    return Undefined.VALUE;
                }
            }
        } else {
            for (Obj argument : args) { // all varargs should be the same size
                if (!argument.getType().is(parameters.get(0))) {
                    return Undefined.VALUE;
                }
            }
        }

        return eval(args);
    }

    protected abstract Obj eval(List<Obj> arguments);
}
