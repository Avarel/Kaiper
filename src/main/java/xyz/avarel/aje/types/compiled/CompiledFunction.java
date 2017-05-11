package xyz.avarel.aje.types.compiled;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.AJELexer;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.others.Undefined;

import java.util.List;
import java.util.function.Function;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction implements AJEFunction<CompiledFunction>, NativeObject<Function<List<Any>, Any>> {

    private final String script;
    private List<String> parameters;

    public CompiledFunction(List<String> parameters, String script) {
        this.script = script;
        this.parameters = parameters;
    }

    @Override
    public Function<List<Any>, Any> toNative() {
        return this::invoke;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public Any invoke(List<Any> args) {
        if (args.size() < parameters.size()) {
            return Undefined.VALUE;
        }

        AJEParser parser = new AJEParser(new AJELexer(script));

        for (int i = 0; i < parameters.size(); i++) {
            parser.getObjects().put(parameters.get(i), args.get(i));
        }

        return parser.parse();
    }
}
