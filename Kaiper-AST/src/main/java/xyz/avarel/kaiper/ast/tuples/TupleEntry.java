package xyz.avarel.kaiper.ast.tuples;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

public class TupleEntry extends Single {
    private final String name;
    private final Single expr;

    public TupleEntry(Position position, String name, Single expr) {
        super(position);
        this.name = name;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public Single getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }
}
