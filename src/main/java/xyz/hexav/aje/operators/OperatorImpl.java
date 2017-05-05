package xyz.hexav.aje.operators;

public abstract class OperatorImpl implements Operator {
    private final String symbol;
    private final int args;
    private final boolean leftAssoc;

    public OperatorImpl(String symbol, int args) {
        this(symbol, args, true);
    }

    public OperatorImpl(String symbol, int args, boolean leftAssoc) {
        this.symbol = symbol;
        this.args = args;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public String toString() {
        return "Op(" + getSymbol() + ", " + getArgs() + ")";
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getArgs() {
        return args;
    }

    @Override
    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}
