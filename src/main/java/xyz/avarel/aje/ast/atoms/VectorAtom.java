package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

import java.util.List;

public class VectorAtom extends Expr {
    private final List<Expr> exprs;

    public VectorAtom(Position position, List<Expr> items) {
        super(position);
        this.exprs = items;
    }

    public List<Expr> getItems() {
        return exprs;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("list\n");
        for (int i = 0; i < exprs.size() - 1; i++) {
            exprs.get(i).ast(builder, indent + (isTail ? "    " : "│   "), false);
            builder.append('\n');
        }
        if (exprs.size() > 0) {
            exprs.get(exprs.size() - 1).ast(builder, indent + (isTail ? "    " : "│   "), true);
        }
    }

    @Override
    public String toString() {
        return "vector";
    }
}
