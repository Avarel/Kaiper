package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public enum BoolAtom implements Expr {
    TRUE(Bool.TRUE),
    FALSE(Bool.FALSE);

    private final Bool value;

    BoolAtom(Bool value) {
        this.value = value;
    }

    public Bool getValue() {
        return value;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "atom " + value;
    }
}
