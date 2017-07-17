package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.variables.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class TypeNode implements Single {
    private final String name;
    private final List<ParameterData> parameters;
    private final Identifier superType;
    private final List<Single> superParameters;
    private final Expr expr;

    public TypeNode(String name, List<ParameterData> parameters,
                    Identifier superType, List<Single> superParameters,
                    Expr expr) {
        this.name = name;
        this.parameters = parameters;
        this.superType = superType;
        this.superParameters = superParameters;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public List<ParameterData> getParameterExprs() {
        return parameters;
    }

    public Identifier getSuperType() {
        return superType;
    }

    public List<Single> getSuperParameters() {
        return superParameters;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("type").append(name != null ? " " + name : "")
                .append('(')
                .append(getParameterExprs().stream().map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .append(')');

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}
