package xyz.avarel.aje.parser;

import xyz.avarel.aje.operators.Precedence;
import xyz.avarel.aje.parser.parsers.*;
import xyz.avarel.aje.pool.DefaultPool;
import xyz.avarel.aje.types.Any;

import java.util.Iterator;
import java.util.Map;

public class AJEParser extends Parser {
    private final Map<String, Any> objects;

    public AJEParser(Iterator<Token> tokens) {
        this(tokens, DefaultPool.copy());
    }

    public AJEParser(Iterator<Token> tokens, Map<String, Any> objects) {
        super(tokens);

        this.objects = objects;

        register(TokenType.LEFT_BRACKET, new SliceParser());
        register(TokenType.LEFT_PAREN, new GroupParser());
        register(TokenType.NAME, new NameParser());
        register(TokenType.NUMERIC, new NumberParser());

        register(TokenType.LEFT_PAREN, new InvocationParser(Precedence.ACCESS));
        register(TokenType.LEFT_BRACKET, new GetIndexParser(Precedence.ACCESS));
        register(TokenType.DOT, new GetParser(Precedence.ACCESS));

        register(TokenType.PLUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Any::plus));
        register(TokenType.MINUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Any::minus));
        register(TokenType.ASTERISK, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Any::times));
        register(TokenType.SLASH, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Any::divide));

        register(TokenType.CARET, new BinaryOperatorParser(Precedence.EXPONENTIAL, false, Any::pow));
    }

    public Any parse() {
        return parse(0);
    }

    public Any parse(int precedence) {
        Token token = eat();
        PrefixParser prefix = getPrefixParsers().get(token.getType());

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
