package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class GroupParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {
        Expr expr = parser.parseExpr(pool);
        parser.eat(TokenType.RIGHT_PAREN);
        return expr;
    }
}
