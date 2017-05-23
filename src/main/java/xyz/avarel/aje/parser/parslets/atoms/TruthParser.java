package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.BoolAtom;
import xyz.avarel.aje.ast.atoms.UndefAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;

public class TruthParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        switch (token.getText()) {
            case "true":
                return BoolAtom.TRUE;
            case "false":
                return BoolAtom.FALSE;
            default:
                return UndefAtom.VALUE;
        }
    }
}
