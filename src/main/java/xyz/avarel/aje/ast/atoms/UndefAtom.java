package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.scope.Scope;

public enum UndefAtom implements Expr {
    VALUE;

    public Undefined getValue() {
        return Undefined.VALUE;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
