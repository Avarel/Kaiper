package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionAtom implements Expr {
    private final String name;
    private final List<String> parameters;
    private final Expr expr;

    public FunctionAtom(List<String> parameters, Expr expr) {
        this(null, parameters, expr);
    }

    public FunctionAtom(String name, List<String> parameters, Expr expr) {
        this.name = name;
        this.parameters = parameters;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ")
                .append("func").append(name != null ? " " + name : "")
                .append('(')
                .append(getParameters().stream().collect(Collectors.joining(", ")))
                .append(')')
                .append('\n');
        expr.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
