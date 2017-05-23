package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class GroupParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        Expr expr = parser.parseExpr();
        parser.eat(TokenType.RIGHT_PAREN);
        return expr;
    }
}
