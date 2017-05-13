package xyz.avarel.aje.parser;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.parser.parslets.*;
import xyz.avarel.aje.parser.parslets.numeric.BinaryNumericParser;
import xyz.avarel.aje.parser.parslets.numeric.UnaryNumericParser;
import xyz.avarel.aje.parser.parslets.truth.BinaryTruthParser;
import xyz.avarel.aje.parser.parslets.truth.UnaryTruthParser;
import xyz.avarel.aje.pool.DefaultPool;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Truth;
import xyz.avarel.aje.types.Type;

import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AJEParser extends Parser {
    private final Map<String, Any> objects;

    public AJEParser(Iterator<Token> tokens) {
        this(tokens, DefaultPool.copy());
    }

    public AJEParser(Iterator<Token> tokens, Map<String, Any> objects) {
        super(tokens);

        this.objects = objects;

        // Basic
        register(TokenType.LEFT_BRACKET, new SliceParser());
        register(TokenType.LEFT_PAREN, new GroupParser());
        register(TokenType.LEFT_BRACE, new LambdaParser());
        register(TokenType.FUNCTION, new FunctionParser());

        register(TokenType.NAME, new NameParser());
        register(TokenType.INT, new NumberParser());
        register(TokenType.DECIMAL, new NumberParser());
        register(TokenType.IMAGINARY, new NumberParser());
        register(TokenType.BOOLEAN, new BooleanParser());


        // Numeric
        register(TokenType.MINUS, new UnaryNumericParser(Any::negative));
        register(TokenType.PLUS, new UnaryNumericParser(Any::identity));
        register(TokenType.PLUS, new BinaryNumericParser(Precedence.ADDITIVE, true, Any::plus));
        register(TokenType.MINUS, new BinaryNumericParser(Precedence.ADDITIVE, true, Any::minus));
        register(TokenType.ASTERISK, new BinaryNumericParser(Precedence.MULTIPLICATIVE, true, Any::times));
        register(TokenType.SLASH, new BinaryNumericParser(Precedence.MULTIPLICATIVE, true, Any::divide));
        register(TokenType.PERCENT, new BinaryNumericParser(Precedence.MULTIPLICATIVE, true, Any::mod));
        register(TokenType.CARET, new BinaryNumericParser(Precedence.EXPONENTIAL, false, Any::pow));

        register(TokenType.EQUALS, new BinaryNumericParser(Precedence.EQUALITY, true, Any::equals));
        register(TokenType.NOT_EQUAL, new BinaryNumericParser(Precedence.EQUALITY, true, (a, b) -> a.equals(b).negative()));
        register(TokenType.GT, new BinaryNumericParser(Precedence.COMPARISON, true, Any::greaterThan));
        register(TokenType.LT, new BinaryNumericParser(Precedence.COMPARISON, true, Any::lessThan));
        register(TokenType.GTE, new BinaryNumericParser(Precedence.COMPARISON, true, (a, b) -> a.equals(b).or(a.greaterThan(b))));
        register(TokenType.LTE, new BinaryNumericParser(Precedence.COMPARISON, true, (a, b) -> a.equals(b).or(a.lessThan(b))));

        // Truth
        register(TokenType.TILDE, new UnaryTruthParser(Truth::negative));
        register(TokenType.BANG, new UnaryTruthParser(Truth::negative));

        register(TokenType.AND, new BinaryTruthParser(Precedence.CONJUNCTION, true, Truth::and));
        register(TokenType.OR, new BinaryTruthParser(Precedence.DISJUNCTION, true, Truth::or));

        // Functional
        register(TokenType.LEFT_PAREN, new InvocationParser(Precedence.ACCESS));
        register(TokenType.LEFT_BRACKET, new GetIndexParser(Precedence.ACCESS));
        register(TokenType.DOT, new AttributeParser(Precedence.ACCESS));

        register(TokenType.ASSIGN, new AssignmentParser());



    }

    public Any compile() {
        Any any;

        do {
            any = parse();
        } while (match(TokenType.LINE));

        if (getLexer().hasNext()) {
            throw new AJEException("Could not parse " + getLexer().next().getText());
        }

        return any;
    }

    public Any parse() {
        return parse(0);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(Type<T> type) {
        return parse(type, 0);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(Type<T> type, int precedence) {
        Any any = parse(precedence);
        if (!any.getType().is(type)) {
            throw new AJEException("Expected type " + type + " but found " + any.getType());
        }
        return (T) any;
    }

    public Any parse(int precedence) {
        Token token = eat();
        PrefixParser<Any> prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw new RuntimeException("Could not parse \"" + token.getText() + "\".");

        Any left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = eat();

            InfixParser<Any, Any> infix = getInfixParsers().get(token.getType());
            if (!infix.keepIdentity()) left = left.identity();
            left = infix.parse(this, left, token);
        }

        return left;
    }

    public Map<String, Any> getObjects() {
        return objects;
    }
}
