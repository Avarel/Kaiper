package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.NameAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.Collections;

public class ImplicitParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        Expr expr = parser.parseInfix(0, new NameAtom("_"));

        return new FunctionAtom(Collections.singletonList(new Parameter("_")), expr);
    }
}
