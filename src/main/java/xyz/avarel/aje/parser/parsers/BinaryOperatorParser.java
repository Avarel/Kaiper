package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Any;

import java.util.function.BinaryOperator;

public class BinaryOperatorParser extends BinaryParser {
    private final BinaryOperator<Any> operator;

    public BinaryOperatorParser(int precedence, boolean leftAssoc, BinaryOperator<Any> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        Any right = parser.parse(getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return operator.apply(left, right);
    }
}
