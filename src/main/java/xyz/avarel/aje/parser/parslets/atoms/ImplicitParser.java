package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.FunctionAtom;
import xyz.avarel.aje.parser.expr.atoms.NameAtom;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.functions.CompiledFunction;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.Collections;

public class ImplicitParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {
        ObjectPool subPool = parser.getObjectPool().subPool();

        Expr expr = parser.parseInfix(0, new NameAtom(subPool, "_"), subPool);

        return new FunctionAtom(new CompiledFunction(Collections.singletonList("_"), expr, subPool));
    }
}
