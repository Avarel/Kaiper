package xyz.avarel.aje.parser.parslets.truth;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Truth;

import java.util.function.UnaryOperator;

public class UnaryTruthParser implements PrefixParser<Truth> {
    private final UnaryOperator<Truth> operator;

    public UnaryTruthParser(UnaryOperator<Truth> operator) {
        this.operator = operator;
    }

    @Override
    public Truth parse(AJEParser parser, Token token) {
        return operator.apply(parser.parse(Truth.TYPE, Precedence.PREFIX));
    }
}
