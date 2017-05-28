package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class SliceOperation extends Expr {
    private final Expr left;
    private final Expr start;
    private final Expr end;
    private final Expr step;

    public SliceOperation(Position position, Expr left, Expr start, Expr end, Expr step) {
        super(position);
        this.left = left;
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getStart() {
        return start;
    }

    public Expr getEnd() {
        return end;
    }

    public Expr getStep() {
        return step;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("slice\n");

        left.ast(builder, indent + (isTail ? "    " : "│   "), false);

        if (start != null) {
            builder.append('\n');
            start.ast("start", builder, indent + (isTail ? "    " : "│   "), false);
        }

        if (end != null) {
            builder.append('\n');
            end.ast("end", builder, indent + (isTail ? "    " : "│   "), false);
        }

        if (step != null) {
            builder.append('\n');
            step.ast("step", builder, indent + (isTail ? "    " : "│   "), true);
        }
    }

    @Override
    public String toString() {
        return "slice operation";
    }
}
