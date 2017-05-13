package xyz.avarel.aje.functional;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Undefined;

import java.util.Arrays;
import java.util.List;

public abstract class NativeFunction extends AJEFunction {
    private final List<Type> parameters;

    public NativeFunction(Type... parameters) {
        this.parameters = Arrays.asList(parameters);
    }

    @Override
    public Any invoke(List<Any> arguments) {
        if (arguments.size() != parameters.size()) {
            return Undefined.VALUE;
        }

        for (int i = 0; i < parameters.size(); i++) {
            if (!arguments.get(i).getType().is(parameters.get(i))) {
                return Undefined.VALUE;
            }
        }

        return eval(arguments);
    }

    public abstract Any eval(List<Any> arguments);
}
