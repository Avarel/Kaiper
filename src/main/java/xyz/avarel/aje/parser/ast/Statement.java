package xyz.avarel.aje.parser.ast;

import xyz.avarel.aje.runtime.Any;

public class Statement implements Expr {
    private final Expr before;
    private final Expr after;

    private boolean hasNext;

    public Statement(Expr before, Expr after) {
        this.before = before;
        this.after = after;

        if (before instanceof Statement) {
            ((Statement) before).hasNext = true;
        }
    }

    @Override
    public Any compute() {
        before.compute();
        return after.compute();
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        before.ast(builder, prefix, false);
        builder.append('\n');
        after.ast(builder, prefix, !hasNext);
    }
}
