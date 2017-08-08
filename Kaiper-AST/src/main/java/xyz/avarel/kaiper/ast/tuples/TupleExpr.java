package xyz.avarel.kaiper.ast.tuples;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

import java.util.Collections;
import java.util.List;

public class TupleExpr extends Single {
    public final List<TupleEntry> entries;

    public TupleExpr(Single convert) {
        this(convert.getPosition(), Collections.singletonList(new TupleEntry(convert.getPosition(), "_0", convert)));
    }

    public TupleExpr(Position position, List<TupleEntry> entries) {
        super(position);
        this.entries = entries;
    }

    public List<TupleEntry> getEntries() {
        return entries;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return entries.toString();
    }
}
