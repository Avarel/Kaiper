package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.util.TreeMap;

public class JavaFunction extends Function {
    private final MatchExpr matchExpr;

    protected JavaFunction(String name) {
        super(name);
        this.matchExpr = new MatchExpr(null, new Identifier(null, "argument"), new TreeMap<>());
    }

    @Override
    public Obj invoke(Obj argument) {
        Scope<String, Obj> subScope = new Scope<>();

        ExprInterpreter.declare(subScope, "argument", argument);

        return Null.VALUE;
//        return matchExpr.accept(visitor, subScope);
    }
}
