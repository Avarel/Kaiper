package xyz.hexav.aje.operators;

import xyz.hexav.aje.defaults.BinaryOperators;
import xyz.hexav.aje.defaults.UnaryOperators;

public class DefaultOperatorMap extends OperatorMap {
    public static final OperatorMap INSTANCE = new DefaultOperatorMap();
    
    private DefaultOperatorMap() {
        // TODO inline operators
//        register(Precedence.ASSIGNMENT, DefaultOperators.VAR_ASSIGNMENT);

        registerPrefix(Precedence.PREFIX, UnaryOperators.UNARY_PLUS);
        registerPrefix(Precedence.PREFIX, UnaryOperators.UNARY_MINUS);
        registerPrefix(Precedence.PREFIX, UnaryOperators.LOGICAL_NOT);
//
        registerBinary(Precedence.RANGE_TO, BinaryOperators.RANGE_TO);

        registerBinary(Precedence.DISJUNCTION, BinaryOperators.LOGICAL_OR);
        registerBinary(Precedence.CONJUNCTION, BinaryOperators.LOGICAL_AND);

        registerBinary(Precedence.ADDITIVE, BinaryOperators.ADD);
        registerBinary(Precedence.ADDITIVE, BinaryOperators.SUBTRACT);

        registerBinary(Precedence.MULTIPLICATIVE, BinaryOperators.MULTIPLY);
        registerBinary(Precedence.MULTIPLICATIVE, BinaryOperators.DIVIDE);
        registerBinary(Precedence.MULTIPLICATIVE, BinaryOperators.PERCENTAGE);
        registerBinary(Precedence.MULTIPLICATIVE, BinaryOperators.REMAINDER);
        registerBinary(Precedence.MULTIPLICATIVE, BinaryOperators.NTH_ROOT);

        registerBinary(Precedence.EQUALITY, BinaryOperators.EQUALS);
        registerBinary(Precedence.EQUALITY, BinaryOperators.NOT_EQUALS);
//
        registerBinary(Precedence.COMPARISON, BinaryOperators.GREATER_OR_EQUAL);
        registerBinary(Precedence.COMPARISON, BinaryOperators.GREATER_THAN);
        registerBinary(Precedence.COMPARISON, BinaryOperators.LESSER_OR_EQUAL);
        registerBinary(Precedence.COMPARISON, BinaryOperators.LESSER_THAN);
//
//        register(Precedence.SHIFT, DefaultOperators.ZERO_FILL_RIGHT_SHIFT);
//        register(Precedence.SHIFT, DefaultOperators.RIGHT_SHIFT);
//        register(Precedence.SHIFT, DefaultOperators.LEFT_SHIFT);
//
////        register(Precedence.UNARY, DefaultOperators.PRE_INCREMENT);
////        register(Precedence.UNARY, DefaultOperators.PRE_DECREMENT);
//        register(Precedence.UNARY, DefaultOperators.BITWISE_COMPLEMENT);

//
        registerBinary(Precedence.EXPONENTIAL, BinaryOperators.EXPONENTATION);
        registerBinary(Precedence.EXPONENTIAL, BinaryOperators.SCIENTIFIC_EX);
//
////        register(Precedence.POSTFIX, DefaultOperators.POST_INCREMENT);
////        register(Precedence.POSTFIX, DefaultOperators.POST_DECREMENT);
//
//        register(Precedence.POSTFIX, DefaultOperators.DEGREES);
        registerBinary(Precedence.INFIX, BinaryOperators.LIST_INDEX);
    }

}
