package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.ast.AssignmentExpr;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class AssignmentParser extends BinaryParser {
    public AssignmentParser() {
        super(Precedence.ASSIGNMENT);
    }

    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence(), pool);
        return new AssignmentExpr(left, right);
    }
}