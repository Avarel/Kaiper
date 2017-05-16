package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.operations.RangeExpr;
import xyz.avarel.aje.parser.lexer.Token;

public class RangeToOperatorParser extends BinaryParser<Expr, Expr> {
    public RangeToOperatorParser() {
        super(Precedence.RANGE_TO);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Expr right = parser.parse(getPrecedence());
        return new RangeExpr(left, right);
    }
}