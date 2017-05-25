package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class NativeFunction extends AJEFunction {
    private final List<Parameter> parameters;
    private final boolean varargs;

    public NativeFunction(Type... types) {
        this.parameters = Arrays.stream(types).map(Parameter::new).collect(Collectors.toList());
        this.varargs = false;
    }

    public NativeFunction(boolean varargs, Type type) {
        this.parameters = Collections.singletonList(new Parameter(type));
        this.varargs = varargs;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "native function";
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
                if (!args.get(i).getType().is((Type) parameters.get(i).getType().compute())) {
                    return Undefined.VALUE;
                }
            }
        } else {
            for (Obj argument : args) { // all varargs should be the same size
                if (!argument.getType().is((Type) parameters.get(0).getType().compute())) {
                    return Undefined.VALUE;
                }
            }
        }

        return eval(args);
    }

    protected abstract Obj eval(List<Obj> arguments);
}
