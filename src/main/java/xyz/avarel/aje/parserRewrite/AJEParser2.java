package xyz.avarel.aje.parserRewrite;

import xyz.avarel.aje.operators.Precedence;
import xyz.avarel.aje.parserRewrite.parsers.*;
import xyz.avarel.aje.pool.DefaultPool;
import xyz.avarel.aje.types.Any;

import java.util.Iterator;
import java.util.Map;

public class AJEParser2 extends Parser {
    private Map<String, Any> objects = DefaultPool.copy();

    public AJEParser2(Iterator<Token> tokens) {
        super(tokens);

        register(TokenType.LEFT_BRACKET, new SliceParser());
        register(TokenType.LEFT_PAREN, new GroupParser());
        register(TokenType.NAME, new NameParser());
        register(TokenType.NUMERIC, new NumberParser());

        register(TokenType.DOT, new GetParser(Precedence.ACCESS));
        register(TokenType.PLUS, new BinaryParser(Precedence.ADDITIVE, false));
        register(TokenType.MINUS, new BinaryParser(Precedence.ADDITIVE, false));
        register(TokenType.ASTERISK, new BinaryParser(Precedence.MULTIPLICATIVE, false));
    }

    public Any parse() {
        return parse(0);
    }

    public Any parse(int precedence) {
        Token token = eat();
        PrefixParselet prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw new RuntimeException("Could not parse \"" + token.getText() + "\".");

        Any left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = eat();

            InfixParser infix = getInfixParsers().get(token.getType());
            left = infix.parse(this, left, token);
        }

        return left;
    }

    public Map<String, Any> getObjects() {
        return objects;
    }
}
