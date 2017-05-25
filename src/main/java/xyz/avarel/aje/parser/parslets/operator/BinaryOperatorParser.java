package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.ast.AssignmentExpr;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.NameAtom;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;

import java.util.function.BinaryOperator;

public class BinaryOperatorParser extends BinaryParser {
    private final BinaryOperator<Obj> operator;

    public BinaryOperatorParser(int precedence, boolean leftAssoc, BinaryOperator<Obj> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (left instanceof NameAtom) {
            if (parser.match(TokenType.ASSIGN)) {
                Expr right = parser.parseExpr(0);
                return new AssignmentExpr(((NameAtom) left).getName(), new BinaryOperation(left, right, operator), false);
            }
        }

        Expr right = parser.parseExpr(getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return new BinaryOperation(left, right, operator);
    }
}