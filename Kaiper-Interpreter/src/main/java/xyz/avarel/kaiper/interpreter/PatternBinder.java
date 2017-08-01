package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.Pair;
import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;

public class PatternBinder implements PatternVisitor<Pair<String, Obj>, Obj> {
    private static final Pair<String, Obj> TRUE_NO_ASSIGN = new Pair<>(null, null);

    private final ExprInterpreter interpreter;
    private final Scope scope;

    private int position = 0;

    private PatternBinder(ExprInterpreter interpreter, Scope scope) {
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public static boolean bind(ExprInterpreter interpreter, PatternCase patternCase, Tuple tuple, Scope scope) {
        if (tuple.size() != patternCase.getPatterns().size()) {
            return false;
        }

        PatternBinder binder = new PatternBinder(interpreter, scope);

        for (Pattern pattern : patternCase.getPatterns()) {
            Pair<String, Obj> result = pattern.accept(binder, tuple);

            if (result != null) {
                if (result != TRUE_NO_ASSIGN) {
                    scope.declare(result.getFirst(), result.getSecond());
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public Pair<String, Obj> accept(WildcardPattern pattern, Obj obj) {
        position++;
        return TRUE_NO_ASSIGN;
    }

    @Override
    public Pair<String, Obj> accept(ValuePattern pattern, Obj obj) {
        if (obj.hasAttr("_" + position)) {
            Obj value = obj.getAttr("_" + position);
            Obj target = pattern.getValue().accept(interpreter, scope);
            position++;
            return value.equals(target) ? TRUE_NO_ASSIGN : null;
        } else {
            return null;
        }
    }

    @Override
    public Pair<String, Obj> accept(VariablePattern pattern, Obj obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else if (obj.hasAttr("_" + position)) {
            value = obj.getAttr("_" + position);
        } else {
            return null;
        }

        position++;
        return new Pair<>(pattern.getName(), value);
    }

    @Override
    public Pair<String, Obj> accept(DefaultPattern pattern, Obj obj) {
        Pair<String, Obj> result = pattern.getDelegate().accept(this, obj);

        if (result == null) {
            Obj value = pattern.getDefaultExpr().accept(interpreter, scope);
            return new Pair<>(pattern.getDelegate().getName(), value);
        } else {
            return result;
        }
    }

    @Override
    public Pair<String, Obj> accept(TuplePattern pattern, Obj obj) {
        return null;
    }
}
