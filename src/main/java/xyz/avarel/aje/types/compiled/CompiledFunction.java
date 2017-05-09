package xyz.avarel.aje.types.compiled;

import xyz.avarel.aje.AJEParser;
import xyz.avarel.aje.pool.DefaultPool;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Type;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction implements Any<CompiledFunction>, NativeObject<Function<List<Any>, Any>> {
    public static final Type<CompiledFunction> TYPE = new Type<>("function");

    private final String script;
    private Map<String, Type> parameters;

    public CompiledFunction(Map<String, Type> parameters, String script) {
        this.script = script;
        this.parameters = parameters;
    }

    @Override
    public Function<List<Any>, Any> toNative() {
        return this::invoke;
    }

    public Map<String, Type> getParameters() {
        return parameters;
    }

    @Override
    public Type<CompiledFunction> getType() {
        return TYPE;
    }

    @Override
    public Any invoke(List<Any> args) {
//        if (args.size() != parameters.size()) {
//            return Undefined.VALUE;
//        }

//        List<Any> _args = new ArrayList<>();
//        for (int i = 0; i < parameters.size(); i++) {
//            _args.add(args.get(i).castUp(parameters.get(i)));
//        }
//
//        if (!parameters.equals(_args.stream().map(Any::getType).collect(Collectors.toList()))) { // Check types
//            return Undefined.VALUE;
//        }

        AJEParser parser = new AJEParser(DefaultPool.INSTANCE.copy(), script);
        return parser.compute();
    }
}
