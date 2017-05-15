package xyz.avarel.aje.parser.lexer;

import java.util.Iterator;

public class LexerProxy extends AJELexer {
    private final Iterator<Token> proxy;

    public LexerProxy(Iterable<Token> proxy) {
        this.proxy = proxy.iterator();
    }

    @Override
    public boolean hasNext() {
        return proxy.hasNext();
    }

    @Override
    public Token next() {
        if (!proxy.hasNext()) return new Token(0, TokenType.EOF);
        return proxy.next();
    }
}
