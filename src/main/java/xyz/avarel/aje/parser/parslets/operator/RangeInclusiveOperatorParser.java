package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.operations.RangeExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;

public class RangeInclusiveOperatorParser extends BinaryParser {
    public RangeInclusiveOperatorParser() {
        super(Precedence.RANGE_TO);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence());
        return new RangeExpr(left, right, false);
    }
}