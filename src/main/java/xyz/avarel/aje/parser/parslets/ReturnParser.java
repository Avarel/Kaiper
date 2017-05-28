package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ReturnExpr;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Undefined;

public class ReturnParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        Expr expr;
        if (parser.peekAny(TokenType.LINE, TokenType.SEMICOLON, TokenType.RIGHT_BRACE)) {
            expr = new ValueAtom(token.getPosition(), Undefined.VALUE);
        } else {
            expr = parser.parseExpr();
        }
        return new ReturnExpr(token.getPosition(), expr);
    }
}
