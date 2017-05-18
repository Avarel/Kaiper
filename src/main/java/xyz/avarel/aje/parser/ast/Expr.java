package xyz.avarel.aje.parser.ast;

import xyz.avarel.aje.runtime.Any;

public interface Expr {
    Any compute();

    default Expr andThen(Expr after) {
        return new Statement(this, after);
    }

    default void ast(StringBuilder builder, String prefix, boolean isTail) {
    }
}
