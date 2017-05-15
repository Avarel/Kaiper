package xyz.avarel.aje.runtime.types.compiled;

import xyz.avarel.aje.runtime.functional.AJEFunction;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.lexer.LexerProxy;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;
import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Undefined;

import java.util.List;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction {
    private final List<String> parameters;
    private final List<Token> tokens;
    private final ObjectPool pool;

    public CompiledFunction(List<String> parameters, List<Token> tokens, ObjectPool pool) {
        this.parameters = parameters;
        this.tokens = tokens;
        this.pool = pool;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public Any<?> invoke(List<Any> args) {
        if (args.size() != parameters.size()) {
            return Undefined.VALUE;
        }

        AJEParser parser = new AJEParser(new LexerProxy(tokens), pool.copy());

        for (int i = 0; i < parameters.size(); i++) {
            parser.getObjects().put(parameters.get(i), args.get(i));
        }

        return parser.compute();
    }

}
