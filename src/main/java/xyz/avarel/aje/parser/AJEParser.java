package xyz.avarel.aje.parser;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.parser.parslets.*;
import xyz.avarel.aje.parser.parslets.any.BinaryAnyParser;
import xyz.avarel.aje.parser.parslets.numeric.BinaryNumericParser;
import xyz.avarel.aje.parser.parslets.numeric.UnaryNumericParser;
import xyz.avarel.aje.parser.parslets.truth.BinaryTruthParser;
import xyz.avarel.aje.parser.parslets.truth.UnaryTruthParser;
import xyz.avarel.aje.pool.ObjectPool;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Truth;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Undefined;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public class AJEParser extends Parser {
    private final ObjectPool objects;

    public AJEParser(Iterator<Token> tokens) {
        this(tokens, new ObjectPool());
    }

    public AJEParser(Iterator<Token> tokens, ObjectPool objects) {
        super(tokens);
        this.objects = objects;

        // BLOCKS
        register(TokenType.LEFT_BRACKET, new SliceParser());
        register(TokenType.LEFT_PAREN, new GroupParser());
        register(TokenType.LEFT_BRACE, new LambdaParser());

        // TYPES
        register(TokenType.NAME, new NameParser());
        register(TokenType.INT, new NumberParser());
        register(TokenType.DECIMAL, new NumberParser());
        register(TokenType.IMAGINARY, new NumberParser());
        register(TokenType.BOOLEAN, new BooleanParser());
        register(TokenType.FUNCTION, new FunctionParser());

        // Numeric
        register(TokenType.MINUS, new UnaryNumericParser(Any::negative));
        register(TokenType.PLUS, new UnaryNumericParser(Any::identity));
        register(TokenType.PLUS, new BinaryNumericParser(Precedence.ADDITIVE, true, Any::plus));
        register(TokenType.MINUS, new BinaryNumericParser(Precedence.ADDITIVE, true, Any::minus));
        register(TokenType.ASTERISK, new BinaryNumericParser(Precedence.MULTIPLICATIVE, true, Any::times));
        register(TokenType.SLASH, new BinaryNumericParser(Precedence.MULTIPLICATIVE, true, Any::divide));
        register(TokenType.PERCENT, new BinaryNumericParser(Precedence.MULTIPLICATIVE, true, Any::mod));
        register(TokenType.CARET, new BinaryNumericParser(Precedence.EXPONENTIAL, false, Any::pow));

        // RELATIONAL
        register(TokenType.EQUALS, new BinaryAnyParser(Precedence.EQUALITY, true, Any::isEqualTo));
        register(TokenType.NOT_EQUAL, new BinaryAnyParser(Precedence.EQUALITY, true, (a, b) -> a.isEqualTo(b).negative()));
        register(TokenType.GT, new BinaryNumericParser(Precedence.COMPARISON, true, Any::greaterThan));
        register(TokenType.LT, new BinaryNumericParser(Precedence.COMPARISON, true, Any::lessThan));
        register(TokenType.GTE, new BinaryNumericParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.greaterThan(b))));
        register(TokenType.LTE, new BinaryNumericParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.lessThan(b))));

        // Truth
        register(TokenType.TILDE, new UnaryTruthParser(Truth::negative));
        register(TokenType.BANG, new UnaryTruthParser(Truth::negative));
        register(TokenType.AND, new BinaryTruthParser(Precedence.CONJUNCTION, true, Truth::and));
        register(TokenType.OR, new BinaryTruthParser(Precedence.DISJUNCTION, true, Truth::or));

        // Functional
        register(TokenType.LEFT_PAREN, new InvocationParser());
        register(TokenType.LEFT_BRACKET, new GetIndexParser());
        register(TokenType.DOT, new AttributeParser());
        register(TokenType.ASSIGN, new AssignmentParser());
    }

    public Any compute() {
        Any any = Undefined.VALUE;

        do {
            // Temporary solution?
            if (match(TokenType.LINE)) continue;
            if (match(TokenType.EOF)) break;
            any = parse();
        } while (match(TokenType.LINE));

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

    public Any parse() {
        return parse(0);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(Type<T> type) {
        return parse(type, 0);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(Type<T> type, int precedence) {
        Any any = parse(precedence).identity();
        if (!any.getType().is(type)) {
            throw error("Expected type " + type + " but found " + any.getType());
        }
        return (T) any;
    }

    public Any parse(int precedence) {
        Token token = eat();
        PrefixParser<Any> prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw error("Could not parse token `" + token.getText() + "`");

        Any left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = eat();

            InfixParser<Any, Any> infix = getInfixParsers().get(token.getType());

            if (infix == null) throw error("Could not parse token `" + token.getText() + "`");

            if (!infix.keepIdentity()) left = left.identity();
            left = infix.parse(this, left, token);
        }

        return left;
    }


    public ObjectPool getObjects() {
        return objects;
    }
}
