package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ReturnExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;

public class ReturnParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        return new ReturnExpr(parser.parseExpr());
    }
}
