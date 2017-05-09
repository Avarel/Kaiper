package xyz.avarel.aje.defaults;

import xyz.avarel.aje.operators.AJEUnaryOperator;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Truth;

public enum UnaryOperators implements AJEUnaryOperator {
    UNARY_PLUS("+") {
        @Override
        public Any apply(Any a) {
            Truth.assertNot(a);
            return a;
        }
    },
    UNARY_MINUS("-") {
        @Override
        public Any apply(Any a) {
            Truth.assertNot(a);
            return a.negative();
        }
    },
    LOGICAL_NOT("!") {
        @Override
        public Any apply(Any a) {
            Truth.assertIs(a);
            return a.negative();
        }
    },

    TO_DEGREES("deg") {
        @Override
        public Any apply(Any any) {
            return Int.of(999);
        }
    };

    private final String symbol;

    UnaryOperators(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
