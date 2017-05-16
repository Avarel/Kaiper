package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.invocation.InvocationExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class InvocationParser extends BinaryParser<Expr, Expr> {
    public InvocationParser() {
        super(Precedence.ACCESS);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        List<Expr> list = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                list.add(parser.parse());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_PAREN);
        }

        return new InvocationExpr(left, list);
    }
}
