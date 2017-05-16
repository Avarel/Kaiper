package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.ListIndexExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class GetIndexParser extends BinaryParser<Expr, Expr> {
    public GetIndexParser() {
        super(Precedence.ACCESS);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Expr index = parser.parse();
        parser.eat(TokenType.RIGHT_BRACKET);
        return new ListIndexExpr(left, index);
    }
}
