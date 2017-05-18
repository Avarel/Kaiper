package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.parser.ast.atoms.BooleanAtom;
import xyz.avarel.aje.parser.ast.atoms.UndefAtom;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class TruthParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {
        switch (token.getText()) {
            case "true":
                return BooleanAtom.TRUE;
            case "false":
                return BooleanAtom.FALSE;
            default:
                return UndefAtom.VALUE;
        }
    }
}
