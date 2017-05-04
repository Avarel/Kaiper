package xyz.hexav.aje.operators;

import xyz.hexav.aje.types.Expression;

import java.util.function.BinaryOperator;

public abstract class Operator implements BinaryOperator<Expression> {
    private final String symbol;
    private final int args;
    private final boolean leftAssoc;

    public Operator(String symbol, int args) {
        this(symbol, args, true);
    }

    public Operator(String symbol, int args, boolean leftAssoc) {
        this.symbol = symbol;
        this.args = args;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public String toString() {
        return "Op(" + getSymbol() + ", " + getArgs() + ")";
    }

    public String getSymbol() {
        return symbol;
    }

    public int getArgs() {
        return args;
    }

    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}
