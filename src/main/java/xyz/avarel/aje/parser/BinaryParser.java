package xyz.avarel.aje.parser;

public abstract class BinaryParser<IN, OUT> implements InfixParser<IN, OUT> {
    private final int precedence;
    private final boolean leftAssoc;

    public BinaryParser(int precedence, boolean leftAssoc) {
        this.precedence = precedence;
        this.leftAssoc = leftAssoc;
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}