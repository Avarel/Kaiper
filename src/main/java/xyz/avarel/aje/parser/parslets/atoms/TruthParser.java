package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.Bool;

public class TruthParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        switch (token.getText()) {
            case "true":
                return new ValueAtom(token.getPosition(), Bool.TRUE);
            case "false":
                return new ValueAtom(token.getPosition(), Bool.FALSE);
            default:
                throw new SyntaxException("Bool atom not of expected value", token.getPosition());
        }
    }
}
