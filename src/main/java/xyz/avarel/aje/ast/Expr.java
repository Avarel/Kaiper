package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;

public interface Expr {
    Obj accept(ExprVisitor visitor, Scope scope);

    default Expr andThen(Expr after) {
        return new Statements(this, after);
    }

    default Obj compute() {
        return accept(new ExprVisitor(), DefaultScope.INSTANCE.subPool());
    }

    default void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(toString());
    }
}
