package xyz.avarel.aje.types.compiled;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Undefined;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction implements NativeObject<Function<List<Any>, Any>> {
    private final List<String> parameters;
    private final List<Token> tokens;

    public CompiledFunction(List<String> parameters, List<Token> tokens) {
        this.parameters = parameters;
        this.tokens = tokens;
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

        AJEParser parser = new AJEParser(new LexerProxy(tokens.iterator()));

        for (int i = 0; i < parameters.size(); i++) {
            parser.getObjects().put(parameters.get(i), args.get(i));
        }

        return parser.parse();
    }

    private static class LexerProxy implements Iterator<Token> {
        private final Iterator<Token> proxy;

        private LexerProxy(Iterator<Token> proxy) {
            this.proxy = proxy;
        }

        @Override
        public boolean hasNext() {
            return proxy.hasNext();
        }

        @Override
        public Token next() {
            if (!proxy.hasNext()) return new Token(TokenType.EOF);
            return proxy.next();
        }
    }
}
