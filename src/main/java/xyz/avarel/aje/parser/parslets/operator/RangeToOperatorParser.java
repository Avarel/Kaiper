package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.ast.operations.RangeExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class RangeToOperatorParser extends BinaryParser {
    public RangeToOperatorParser() {
        super(Precedence.RANGE_TO);
    }

    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence(), pool);
        return new RangeExpr(left, right);
    }
}