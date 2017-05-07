package xyz.hexav.aje.operators;

public interface AJEBinaryOperator extends ImplicitBinaryOperator {
    String getSymbol();
    boolean isLeftAssoc();
}
