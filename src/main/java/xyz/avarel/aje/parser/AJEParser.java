package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public class AJEParser extends Parser {
    private final ObjectPool pool;

    public AJEParser(Iterator<Token> tokens) {
        this(tokens, new ObjectPool());
    }

    public AJEParser(Iterator<Token> tokens, ObjectPool objectPool) {
        super(tokens, DefaultGrammar.INSTANCE);
        this.pool = objectPool;
    }








    public Expr compile() {
        return compile(true);
    }

    public Expr compile(boolean complete) {
        return compile(pool, complete);
    }

    public Expr compile(ObjectPool pool, boolean complete) {
        return compile(pool, complete, true);
    }

    public Expr compile(ObjectPool pool, boolean complete, boolean statements) {
        Expr any = parse(pool);

        if (statements) {
            while (match(TokenType.LINE)) {
                // Temporary solution?
                if (match(TokenType.LINE)) continue;
                if (match(TokenType.EOF)) break;

                any = any.andThen(parse(pool));
            }
        }

        if (complete && !getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw error("Did not parse " + t.getText(), t.getPos());
            }
        }

        return any;
    }

    public Expr parse(ObjectPool objectPool) {
        return parse(0, objectPool);
    }

    public Expr parse(int precedence, ObjectPool pool) {
        Token token = eat();
        PrefixParser prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw error("Could not parse token `" + token.getText() + "`");

        Expr left = prefix.parse(this, pool, token);

        while (precedence < getPrecedence()) {
            token = eat();

            InfixParser infix = getInfixParsers().get(token.getType());

            if (infix == null) throw error("Could not parse token `" + token.getText() + "`");

            left = infix.parse(this, pool, left, token);
        }

        return left;
    }


    public ObjectPool getObjectPool() {
        return pool;
    }
}
