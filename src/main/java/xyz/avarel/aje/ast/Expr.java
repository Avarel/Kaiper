package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.DefaultScope;
import xyz.avarel.aje.runtime.pool.Scope;

public interface Expr {
    default Expr andThen(Expr after) {
        return new Statement(this, after);
    }

    default Obj compute() {
        return accept(new ExprVisitor(), DefaultScope.INSTANCE.subPool());
    }

    Obj accept(ExprVisitor visitor, Scope scope);

    default void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(toString());
    }
}
