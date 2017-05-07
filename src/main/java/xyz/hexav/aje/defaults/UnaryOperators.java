package xyz.hexav.aje.defaults;

import xyz.hexav.aje.operators.AJEUnaryOperator;
import xyz.hexav.aje.types.others.Truth;
import xyz.hexav.aje.types.OperableValue;

public enum UnaryOperators implements AJEUnaryOperator {
    UNARY_PLUS("+") {
        @Override
        public OperableValue apply(OperableValue a) {
            Truth.assertNot(a);
            return a;
        }
    },
    UNARY_MINUS("-") {
        @Override
        public OperableValue apply(OperableValue a) {
            Truth.assertNot(a);
            return a.negative();
        }
    },
    LOGICAL_NOT("!") {
        @Override
        public OperableValue apply(OperableValue a) {
            Truth.assertIs(a);
            return a.negative();
        }
    },;

    private final String symbol;

    UnaryOperators(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
