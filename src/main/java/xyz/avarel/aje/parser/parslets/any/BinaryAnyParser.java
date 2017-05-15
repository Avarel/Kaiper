package xyz.avarel.aje.parser.parslets.any;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.types.Any;

import java.util.function.BinaryOperator;

public class BinaryAnyParser extends BinaryParser<Any, Any> {
    private final BinaryOperator<Any> operator;

    public BinaryAnyParser(int precedence, boolean leftAssoc, BinaryOperator<Any> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        Any right = parser.parse(getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return operator.apply(left, right);
    }
}