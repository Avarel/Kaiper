package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class ValueAtom implements Expr {
    private final Obj value;

    public ValueAtom(Obj value) {
        this.value = value;
    }

    public Obj getValue() {
        return value;
    }

    @Override
    public Obj compute() {
        return getValue();
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
