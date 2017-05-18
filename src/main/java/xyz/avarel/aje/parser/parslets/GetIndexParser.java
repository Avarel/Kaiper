package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.ast.ListIndexExpr;
import xyz.avarel.aje.parser.ast.SublistExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class GetIndexParser extends BinaryParser {
    public GetIndexParser() {
        super(Precedence.ACCESS);
    }

    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Expr left, Token token) {
        Expr index = parser.parseExpr(pool);

        if (parser.match(TokenType.COLON)) {
            Expr end = parser.parseExpr(pool);
            parser.eat(TokenType.RIGHT_BRACKET);
            return new SublistExpr(left, index, end);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new ListIndexExpr(left, index);
    }
}
