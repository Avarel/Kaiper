package xyz.avarel.aje.parser.parslets.numeric;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Numeric;

import java.util.function.BinaryOperator;

public class BinaryNumericParser extends BinaryParser<Any, Any> {
    private final BinaryOperator<Any> operator;

    public BinaryNumericParser(int precedence, boolean leftAssoc, BinaryOperator<Any> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        Any right = parser.parse(Numeric.TYPE, getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return Numeric.process(left, right, operator);
    }
}