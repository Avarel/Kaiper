package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.expr.AttributeExpr;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class AttributeParser extends BinaryParser<Expr, Expr> {
    public AttributeParser() {
        super(Precedence.ACCESS, true);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Token name = parser.eat(TokenType.NAME);
        return new AttributeExpr(left, name.getText());
    }
}
