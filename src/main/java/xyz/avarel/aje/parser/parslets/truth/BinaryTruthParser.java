package xyz.avarel.aje.parser.parslets.truth;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Truth;

import java.util.function.BinaryOperator;

public class BinaryTruthParser extends BinaryParser<Truth, Truth> {
    private final BinaryOperator<Truth> operator;

    public BinaryTruthParser(int precedence, boolean leftAssoc, BinaryOperator<Truth> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Truth parse(AJEParser parser, Truth left, Token token) {
        Truth right = parser.parse(Truth.TYPE, getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return operator.apply(left, right);
    }
}
