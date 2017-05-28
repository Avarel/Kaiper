package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionAtom implements Expr {
    private final String name;
    private final List<Parameter> parameters;
    private final Expr expr;

    public FunctionAtom(List<Parameter> parameters, Expr expr) {
        this(null, parameters, expr);
    }

    public FunctionAtom(String name, List<Parameter> parameters, Expr expr) {
        this.name = name;
        this.parameters = parameters;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
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
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("func").append(name != null ? " " + name : "")
                .append('(')
                .append(getParameters().stream().map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .append(')')
                .append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "function";
    }
}
