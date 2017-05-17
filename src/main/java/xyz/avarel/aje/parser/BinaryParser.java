package xyz.avarel.aje.parser;

public abstract class BinaryParser implements InfixParser {
    private final int precedence;
    private final boolean leftAssoc;

    public BinaryParser(int precedence) {
        this(precedence, true);
    }

    public BinaryParser(int precedence, boolean leftAssoc) {
        this.precedence = precedence;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}