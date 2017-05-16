package xyz.avarel.aje.parser.parslets.types;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atom.NameExpr;
import xyz.avarel.aje.parser.lexer.Token;

public class NameParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        return new NameExpr(parser.getObjects(), token.getText());
    }
}
