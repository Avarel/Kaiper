package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.Pair;
import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public class PatternBinder implements PatternVisitor<Pair<String, Obj>, Obj> {
    private static final Pair<String, Obj> TRUE_NO_ASSIGN = new Pair<>(null, null);

    private final ExprInterpreter interpreter;
    private final Scope scope;

    private int position = 0;

    public PatternBinder(ExprInterpreter interpreter, Scope scope) {
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean bind(PatternCase patternCase, Tuple tuple) {
        PatternBinder binder = new PatternBinder(interpreter, scope);

        List<Pair<String, Obj>> results = new ArrayList<>();
        for (Pattern pattern : patternCase.getPatterns()) {
            Pair<String, Obj> result = pattern.accept(binder, tuple);

            if (result != null) {
                if (result != TRUE_NO_ASSIGN) {
                    results.add(result);
                }
            } else {
                return false;
            }
        }

        for (Pair<String, Obj> result : results) {
            scope.declare(result.getFirst(), result.getSecond());
        }

        return true;
    }

    @Override
    public Pair<String, Obj> accept(WildcardPattern pattern, Obj context) {
        position++;
        return TRUE_NO_ASSIGN;
    }

    @Override
    public Pair<String, Obj> accept(ValuePattern pattern, Obj context) {
        if (context.hasAttr("_" + position)) {
            Obj value = context.getAttr("_" + position);
            Obj target = pattern.getValue().accept(interpreter, scope);
            position++;
            return value.equals(target) ? TRUE_NO_ASSIGN : null;
        } else {
            return null;
        }
    }

    @Override
    public Pair<String, Obj> accept(VariablePattern pattern, Obj context) {
        Obj value;

        if (context.hasAttr(pattern.getName())) {
            value = context.getAttr(pattern.getName());
        } else if (context.hasAttr("_" + position)) {
            value = context.getAttr("_" + position);
        } else {
            return null;
        }

        position++;
        return new Pair<>(pattern.getName(), value);
    }

    @Override
    public Pair<String, Obj> accept(DefaultPattern pattern, Obj context) {
        Pair<String, Obj> result = pattern.getDelegate().accept(this, context);

        if (result == null) {
            Obj value = pattern.getDefaultExpr().accept(interpreter, scope);
            return new Pair<>(pattern.getDelegate().getName(), value);
        } else {
            return result;
        }
    }

    @Override
    public Pair<String, Obj> accept(RestPattern pattern, Obj obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            Obj val = obj.getAttr(pattern.getName());
            value = val instanceof Array ? val : Array.of(val);
        } else if (obj.hasAttr("_" + position)) {
            Array array = Array.of(obj.getAttr("_" + position));

            position++;

            while (obj.hasAttr("_" + position)) {
                array.add(obj.getAttr("_" + position));
                position++;
            }

            value = array;
        } else { // empty
            return new Pair<>(pattern.getName(), new Array());
        }

        return new Pair<>(pattern.getName(), value);
    }

    @Override
    public Pair<String, Obj> accept(TuplePattern pattern, Obj context) {
        return null;
    }
}
