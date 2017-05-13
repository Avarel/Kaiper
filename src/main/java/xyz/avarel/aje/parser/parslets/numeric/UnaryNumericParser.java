package xyz.avarel.aje.parser.parslets.numeric;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Numeric;

import java.util.function.UnaryOperator;

public class UnaryNumericParser implements PrefixParser<Any> {
    private final UnaryOperator<Any> operator;

    public UnaryNumericParser(UnaryOperator<Any> operator) {
        this.operator = operator;
    }

    @Override
    public Any parse(AJEParser parser, Token token) {
        return operator.apply(parser.parse(Numeric.TYPE, Precedence.PREFIX));
    }
}
