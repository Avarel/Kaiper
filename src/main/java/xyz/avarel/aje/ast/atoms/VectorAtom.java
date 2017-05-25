package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

import java.util.List;

public class VectorAtom implements Expr {
    private final List<Expr> exprs;

    public VectorAtom(List<Expr> items) {
        this.exprs = items;
    }

    public List<Expr> getItems() {
        return exprs;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("list\n");
        for (int i = 0; i < exprs.size() - 1; i++) {
            exprs.get(i).ast(builder, prefix + (isTail ? "    " : "│   "), false);
            builder.append('\n');
        }
        if (exprs.size() > 0) {
            exprs.get(exprs.size() - 1).ast(builder, prefix + (isTail ? "    " : "│   "), true);
        }
    }

    @Override
    public String toString() {
        return "vector";
    }
}
