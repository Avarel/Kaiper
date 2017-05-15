package xyz.avarel.aje.runtime.functional;

import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Type;
import xyz.avarel.aje.runtime.types.Undefined;

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
    public Any<?> invoke(List<Any> arguments) {
        if (!varargs && arguments.size() != parameters.size()) {
            return Undefined.VALUE;
        }

        if (!varargs) {
            for (int i = 0; i < parameters.size(); i++) {
                if (!arguments.get(i).getType().is(parameters.get(i))) {
                    return Undefined.VALUE;
                }
            }
        } else {
            for (Any argument : arguments) { // all varargs should be the same size
                if (!argument.getType().is(parameters.get(0))) {
                    return Undefined.VALUE;
                }
            }
        }

        return eval(arguments);
    }

    protected abstract Any<?> eval(List<Any> arguments);
}
