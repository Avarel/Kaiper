package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.BooleanAtom;
import xyz.avarel.aje.parser.expr.atoms.UndefAtom;
import xyz.avarel.aje.parser.lexer.Token;

public class TruthParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        switch(token.getText()) {
            case "true": return BooleanAtom.TRUE;
            case "false": return BooleanAtom.FALSE;
            default: return UndefAtom.VALUE;
        }
    }
}
