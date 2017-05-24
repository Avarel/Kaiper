package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

public class NameAtom implements Expr {
    private final String name;

    public NameAtom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "var " + name;
    }
}
