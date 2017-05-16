package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.TruthExpr;
import xyz.avarel.aje.parser.expr.atoms.UndefExpr;
import xyz.avarel.aje.parser.lexer.Token;

public class TruthParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        switch(token.getText()) {
            case "true": return TruthExpr.TRUE;
            case "false": return TruthExpr.FALSE;
            default: return UndefExpr.VALUE;
        }
    }
}
