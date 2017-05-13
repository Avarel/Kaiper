package xyz.avarel.aje.parser;

public abstract class BinaryParser<IN, OUT> implements InfixParser<IN, OUT> {
    private final int precedence;
    private final boolean keepIdentity;
    private final boolean leftAssoc;

    public BinaryParser(int precedence, boolean leftAssoc) {
        this(precedence, leftAssoc, false);
    }

    public BinaryParser(int precedence, boolean leftAssoc, boolean keepIdentity) {
        this.precedence = precedence;
        this.keepIdentity = keepIdentity;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssoc() {
        return leftAssoc;
    }

    @Override
    public boolean keepIdentity() {
        return keepIdentity;
    }
}