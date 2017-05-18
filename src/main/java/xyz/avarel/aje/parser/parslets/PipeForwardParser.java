package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.ast.invocation.PipeForwardExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class PipeForwardParser extends BinaryParser {
    public PipeForwardParser() {
        super(Precedence.PIPE_FORWARD);
    }

    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence(), pool);
        return new PipeForwardExpr(left, right);
    }
}
