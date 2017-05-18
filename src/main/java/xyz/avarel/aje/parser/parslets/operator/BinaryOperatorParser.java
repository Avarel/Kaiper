package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.ast.operations.BinaryOperation;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.function.BinaryOperator;

public class BinaryOperatorParser extends BinaryParser {
    private final BinaryOperator<Any> operator;

    public BinaryOperatorParser(int precedence, boolean leftAssoc, BinaryOperator<Any> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence() - (isLeftAssoc() ? 0 : 1), pool);
        return new BinaryOperation(left, right, operator);
    }
}