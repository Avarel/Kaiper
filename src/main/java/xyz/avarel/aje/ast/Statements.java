package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class Statements implements Expr {
    private final Expr before;
    private final Expr after;

    private boolean hasNext;

    public Statements(Expr before, Expr after) {
        this.before = before;
        this.after = after;

        if (before instanceof Statements) {
            ((Statements) before).hasNext = true;
        }
    }

    public Expr getBefore() {
        return before;
    }

    public Expr getAfter() {
        return after;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        before.ast(builder, indent, false);
        builder.append('\n');
        after.ast(builder, indent, !hasNext);
    }

    @Override
    public String toString() {
        return "statements";
    }
}
