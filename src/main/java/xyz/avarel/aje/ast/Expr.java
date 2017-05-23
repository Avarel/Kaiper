package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

public interface Expr {
    default Expr andThen(Expr after) {
        return new Statement(this, after);
    }

    Obj accept(ExprVisitor visitor, Scope scope);

    void ast(StringBuilder builder, String prefix, boolean isTail);
}
