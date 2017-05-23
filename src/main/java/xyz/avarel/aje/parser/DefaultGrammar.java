package xyz.avarel.aje.parser;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.parser.parslets.*;
import xyz.avarel.aje.parser.parslets.atoms.*;
import xyz.avarel.aje.parser.parslets.operator.BinaryOperatorParser;
import xyz.avarel.aje.parser.parslets.operator.RangeToOperatorParser;
import xyz.avarel.aje.parser.parslets.operator.UnaryOperatorParser;
import xyz.avarel.aje.runtime.Obj;

public class DefaultGrammar extends Grammar {
    public static final Grammar INSTANCE = new DefaultGrammar();

    private DefaultGrammar() {
        // BLOCKS
        register(TokenType.LEFT_BRACKET, new SliceParser());
        register(TokenType.LEFT_PAREN, new GroupParser());
        register(TokenType.LEFT_BRACE, new LambdaParser());

        // ATOMS
        register(TokenType.NAME, new NameParser());
        register(TokenType.INT, new NumberParser());
        register(TokenType.DECIMAL, new NumberParser());
        register(TokenType.IMAGINARY, new NumberParser());
        register(TokenType.BOOLEAN, new TruthParser());
        register(TokenType.FUNCTION, new FunctionParser());

        register(TokenType.UNDERSCORE, new ImplicitParser());

        // Numeric
        register(TokenType.MINUS, new UnaryOperatorParser(Obj::negative));
        register(TokenType.PLUS, new UnaryOperatorParser(Obj::identity));
        register(TokenType.PLUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Obj::plus));
        register(TokenType.MINUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Obj::minus));
        register(TokenType.ASTERISK, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::times));
        register(TokenType.SLASH, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::divide));
        register(TokenType.PERCENT, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::mod));
        register(TokenType.CARET, new BinaryOperatorParser(Precedence.EXPONENTIAL, false, Obj::pow));

        // RELATIONAL
        register(TokenType.EQUALS, new BinaryOperatorParser(Precedence.EQUALITY, true, Obj::isEqualTo));
        register(TokenType.NOT_EQUAL, new BinaryOperatorParser(Precedence.EQUALITY, true, (a, b) -> a.isEqualTo(b).negate()));
        register(TokenType.GT, new BinaryOperatorParser(Precedence.COMPARISON, true, Obj::greaterThan));
        register(TokenType.LT, new BinaryOperatorParser(Precedence.COMPARISON, true, Obj::lessThan));
        register(TokenType.GTE, new BinaryOperatorParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.greaterThan(b))));
        register(TokenType.LTE, new BinaryOperatorParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.lessThan(b))));

        // Truth
        register(TokenType.BANG, new UnaryOperatorParser(Obj::negate));
        register(TokenType.AND, new BinaryOperatorParser(Precedence.CONJUNCTION, true, Obj::and));
        register(TokenType.OR, new BinaryOperatorParser(Precedence.DISJUNCTION, true, Obj::or));

        register(TokenType.RANGE_TO, new RangeToOperatorParser());

        // Functional
        register(TokenType.LEFT_PAREN, new InvocationParser());

        register(TokenType.LEFT_BRACKET, new GetIndexParser());
        register(TokenType.DOT, new AttributeParser());
        //register(TokenType.ASSIGN, new AssignmentParser());
        register(TokenType.PIPE_FORWARD, new PipeForwardParser());
    }
}
