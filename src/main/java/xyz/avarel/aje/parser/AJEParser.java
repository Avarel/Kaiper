package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public class AJEParser extends Parser {
    private final ObjectPool objects;

    public AJEParser(Iterator<Token> tokens) {
        this(tokens, new ObjectPool());
    }

    public AJEParser(Iterator<Token> tokens, ObjectPool objects) {
        super(tokens, DefaultGrammar.INSTANCE);
        this.objects = objects;
    }

    public Expr compile() {
        Expr any = parse();

        while (match(TokenType.LINE)) {
            // Temporary solution?
            if (match(TokenType.LINE)) continue;
            if (match(TokenType.EOF)) break;

            any = any.andThen(parse());
        }

        if (!getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw error("Did not parse " + t.getText(), t.getPos());
            }
        }
//        if (getLexer().hasNext()) {
//            Token t = getLexer().next();
//            throw error("Did not parse " + t.getText(), t.getPos());
//        }

        return any;
    }

    public Expr parse() {
        return parse(0);
    }

    public Expr parse(int precedence) {
        Token token = eat();
        PrefixParser<Expr> prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw error("Could not parse token `" + token.getText() + "`");

        Expr left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = eat();

            InfixParser<Expr, Expr> infix = getInfixParsers().get(token.getType());

            if (infix == null) throw error("Could not parse token `" + token.getText() + "`");

//            if (!infix.keepIdentity()) left = left.identity();
            left = infix.parse(this, left, token);
        }

        return left;
    }


    public ObjectPool getObjects() {
        return objects;
    }
}
