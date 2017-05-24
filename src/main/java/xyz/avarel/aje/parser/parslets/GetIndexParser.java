package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.GetExpr;
import xyz.avarel.aje.ast.SublistExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class GetIndexParser extends BinaryParser {
    public GetIndexParser() {
        super(Precedence.ACCESS);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Expr index = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            Expr end = parser.parseExpr();
            parser.eat(TokenType.RIGHT_BRACKET);
            return new SublistExpr(left, index, end);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new GetExpr(left, index);
    }
}
