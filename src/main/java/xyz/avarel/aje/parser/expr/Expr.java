package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.Any;

public interface Expr {
    Any compute();

    default Expr andThen(Expr other) {
        final Expr me = this;
        return new Expr() {
            @Override
            public Any compute() {
                me.compute();
                return other.compute();
            }

            @Override
            public String toString() {
                return me + " then " + other;
            }
        };
    }
}
