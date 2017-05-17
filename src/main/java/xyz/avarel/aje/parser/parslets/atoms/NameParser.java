package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.NameAtom;
import xyz.avarel.aje.parser.expr.atoms.ValueAtom;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.functions.CompiledFunction;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.Collections;

public class NameParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {

        if (parser.match(TokenType.ARROW)) {
            ObjectPool f_pool = parser.getObjectPool().subpool();

            Expr expr = parser.compile(f_pool, false, false);

            return new ValueAtom(new CompiledFunction(Collections.singletonList(token.getText()), expr, f_pool));
        }

        return new NameAtom(pool, token.getText());
    }
}
