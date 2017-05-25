package xyz.avarel.aje.ast.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class DeclarationExpr extends AssignmentExpr {
    public DeclarationExpr(String name, Expr expr) {
        super(name, expr);
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("declare\n");
        builder.append(prefix).append(isTail ? "    " : "│   ").append("├── ").append(getName());
        builder.append('\n');
        getExpr().ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "declaration";
    }
}
