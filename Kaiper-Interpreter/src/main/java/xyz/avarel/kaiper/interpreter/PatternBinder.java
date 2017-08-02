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
    private static final Pair<String, Obj> SUCCESS_NO_ASSIGNMENT = new Pair<>(null, null);

    private final ExprInterpreter interpreter;
    private final Scope scope;

    private int position = 0;

    public PatternBinder(ExprInterpreter interpreter, Scope scope) {
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean bind(PatternCase patternCase, Tuple tuple) {
        List<Pair<String, Obj>> results = new ArrayList<>();
        for (Pattern pattern : patternCase.getPatterns()) {
            Pair<String, Obj> result = pattern.accept(this, tuple);

            if (result != null) {
                if (result != SUCCESS_NO_ASSIGNMENT) {
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
    public Pair<String, Obj> visit(PatternCase patternCase, Obj obj) {
        if (obj.hasAttr("_" + position)) {
            Obj value = obj.getAttr("_" + position);

            position++;

            Tuple tuple = value instanceof Tuple ? (Tuple) value : new Tuple(value);

            if (new PatternBinder(interpreter, scope).bind(patternCase, tuple)) {
                return SUCCESS_NO_ASSIGNMENT;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Pair<String, Obj> visit(WildcardPattern pattern, Obj obj) {
        position++;
        return SUCCESS_NO_ASSIGNMENT;
    }

    @Override
    public Pair<String, Obj> visit(ValuePattern pattern, Obj obj) {
        System.out.println(obj);
        System.out.println(pattern);

        if (obj.hasAttr("_" + position)) {
            Obj value = obj.getAttr("_" + position);
            Obj target = pattern.getValue().accept(interpreter, scope);
            position++;
            return value.equals(target) ? SUCCESS_NO_ASSIGNMENT : null;
        } else {
            return null;
        }
    }

    @Override
    public Pair<String, Obj> visit(VariablePattern pattern, Obj obj) {
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
    public Pair<String, Obj> visit(DefaultPattern pattern, Obj obj) {
        Pair<String, Obj> result = pattern.getDelegate().accept(this, obj);

        if (result == null) {
            Obj value = pattern.getDefaultExpr().accept(interpreter, scope);
            return new Pair<>(pattern.getDelegate().getName(), value);
        } else {
            return result;
        }
    }

    @Override
    public Pair<String, Obj> visit(RestPattern pattern, Obj obj) {
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
    public Pair<String, Obj> visit(TuplePattern pattern, Obj obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else {
            return null;
        }

        position++;

        Tuple tuple = new Tuple(value);

        return pattern.getPattern().accept(new PatternBinder(interpreter, scope), tuple);
    }
}
