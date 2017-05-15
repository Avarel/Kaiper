package xyz.avarel.aje.parser.parslets.types;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.UndefExpr;
import xyz.avarel.aje.parser.expr.ValueExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.types.Truth;

public class BooleanParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        switch(token.getText()) { // todo create specialized valueExprs for booleans
            case "true": return new ValueExpr(Truth.TRUE);
            case "false": return new ValueExpr(Truth.FALSE);
            default: return UndefExpr.VALUE;
        }
    }
}
