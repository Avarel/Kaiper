package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.operations.UnaryOperation;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.Any;

import java.util.function.UnaryOperator;

public class UnaryOperatorParser implements PrefixParser<Expr> {
    private final UnaryOperator<Any> operator;

    public UnaryOperatorParser(UnaryOperator<Any> operator) {
        this.operator = operator;
    }

    @Override
    public Expr parse(AJEParser parser, Token token) {
        Expr left = parser.parse(Precedence.PREFIX);
        return new UnaryOperation(left, operator);
    }
}
