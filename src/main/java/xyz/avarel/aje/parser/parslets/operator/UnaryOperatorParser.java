package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.Obj;

import java.util.function.UnaryOperator;

public class UnaryOperatorParser implements PrefixParser {
    private final UnaryOperator<Obj> operator;

    public UnaryOperatorParser(UnaryOperator<Obj> operator) {
        this.operator = operator;
    }

    @Override
    public Expr parse(AJEParser parser, Token token) {
        Expr left = parser.parseExpr(Precedence.PREFIX);
        return new UnaryOperation(token.getPosition(), left, operator);
    }
}
