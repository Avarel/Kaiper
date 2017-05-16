package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.Any;

public interface Expr {
    Any compute();

    default Expr andThen(Expr other) {
        return () -> {
            compute();
            return other.compute();
        };
    }
}
