package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.lexer.LexerProxy;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;
import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction {
    private final List<String> parameters;
    private final List<Token> tokens;
    private final ObjectPool pool;
    private final AJEParser parser;
    private Expr expr;

    public CompiledFunction(List<String> parameters, List<Token> tokens, ObjectPool pool) {
        this.parameters = parameters;
        this.tokens = tokens;
        this.pool = pool;
        this.parser = new AJEParser(new LexerProxy(tokens), pool.copy());
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    @Override
    public String toString() {
        return "function(" + parameters.stream().collect(Collectors.joining(",")) + ")";
    }

    @Override
    public Any invoke(List<Any> args) {
        if (args.size() != getArity()) {
            return Undefined.VALUE;
        }

        if (expr == null) {
            expr = parser.compile();
        }

        for (int i = 0; i < parameters.size(); i++) {
            parser.getObjects().put(parameters.get(i), args.get(i));
        }

        return expr.compute();
    }

}
