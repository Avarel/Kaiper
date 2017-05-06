package xyz.hexav.aje.operators;

import xyz.hexav.aje.defaults.BinaryOperators;
import xyz.hexav.aje.defaults.UnaryOperators;

public class DefaultOperatorMap extends OperatorMap {
    public static final OperatorMap INSTANCE = new DefaultOperatorMap();
    
    private DefaultOperatorMap() {
        // TODO inline operators
//        register(Precedence.ASSIGNMENT, DefaultOperators.VAR_ASSIGNMENT);

        register(UnaryOperators.UNARY_PLUS);
        register(UnaryOperators.UNARY_MINUS);
        register(UnaryOperators.LOGICAL_NOT);
//


        register(Precedence.LOGICAL_OR, BinaryOperators.LOGICAL_OR);
        register(Precedence.LOGICAL_AND, BinaryOperators.LOGICAL_AND);

        register(Precedence.ADDITIVE, BinaryOperators.ADD);
        register(Precedence.ADDITIVE, BinaryOperators.SUBTRACT);

        register(Precedence.MULTIPLICATIVE, BinaryOperators.MULTIPLY);
        register(Precedence.MULTIPLICATIVE, BinaryOperators.DIVIDE);
        register(Precedence.MULTIPLICATIVE, BinaryOperators.PERCENTAGE);
        register(Precedence.MULTIPLICATIVE, BinaryOperators.REMAINDER);
        //register(Precedence.MULTIPLICATIVE, DefaultOperators.MODULUS);
        //register(Precedence.MULTIPLICATIVE, DefaultOperators.N_ROOT);

        register(Precedence.EQUALITY, BinaryOperators.EQUALS);
        register(Precedence.EQUALITY, BinaryOperators.NOT_EQUALS);
//
        register(Precedence.RELATIONAL, BinaryOperators.GREATER_OR_EQUAL);
        register(Precedence.RELATIONAL, BinaryOperators.GREATER_THAN);
        register(Precedence.RELATIONAL, BinaryOperators.LESSER_OR_EQUAL);
        register(Precedence.RELATIONAL, BinaryOperators.LESSER_THAN);
//
//        register(Precedence.SHIFT, DefaultOperators.ZERO_FILL_RIGHT_SHIFT);
//        register(Precedence.SHIFT, DefaultOperators.RIGHT_SHIFT);
//        register(Precedence.SHIFT, DefaultOperators.LEFT_SHIFT);
//
////        register(Precedence.UNARY, DefaultOperators.PRE_INCREMENT);
////        register(Precedence.UNARY, DefaultOperators.PRE_DECREMENT);
//        register(Precedence.UNARY, DefaultOperators.BITWISE_COMPLEMENT);

//
        register(Precedence.EXPONENTIAL, BinaryOperators.EXPONENTATION);
        register(Precedence.EXPONENTIAL, BinaryOperators.SCIENTIFIC_EX);
//
////        register(Precedence.POSTFIX, DefaultOperators.POST_INCREMENT);
////        register(Precedence.POSTFIX, DefaultOperators.POST_DECREMENT);
//
//        register(Precedence.POSTFIX, DefaultOperators.DEGREES);
//        register(Precedence.POSTFIX, DefaultOperators.LIST_INDEX);
    }

}
