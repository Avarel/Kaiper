package xyz.avarel.aje.operators;

public interface AJEBinaryOperator extends ImplicitBinaryOperator {
    String getSymbol();
    boolean isLeftAssoc();
}
