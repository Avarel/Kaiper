package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.ReturnException;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;

public interface Expr {
    Obj accept(ExprVisitor visitor, Scope scope);

    default Expr andThen(Expr after) {
        return new Statements(this, after);
    }

    default Obj compute() {
        try {
            return accept(new ExprVisitor(), DefaultScope.INSTANCE.subPool());
        } catch (ReturnException re) {
            return re.getValue();
        }
    }

    default void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append(toString());
    }

    default void ast(String label, StringBuilder builder, String indent, boolean tail) {
        builder.append(indent).append(tail ? "└── " : "├── ").append(label).append(":\n");
        ast(builder, indent + (tail ? "    " : "│   "), true);
    }
}
