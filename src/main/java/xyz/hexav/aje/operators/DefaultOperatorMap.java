package xyz.hexav.aje.operators;

import xyz.hexav.aje.defaults.DefaultOperators;

public class DefaultOperatorMap extends OperatorMap {
    public static final OperatorMap INSTANCE = new DefaultOperatorMap();
    
    private DefaultOperatorMap() {
        // TODO inline operators
//        register(Precedence.ASSIGNMENT, DefaultOperators.VAR_ASSIGNMENT);
//
        register(Precedence.LOGICAL_OR, DefaultOperators.LOGICAL_OR);
        register(Precedence.LOGICAL_AND, DefaultOperators.LOGICAL_AND);

        register(Precedence.ADDITIVE, DefaultOperators.ADD);
        register(Precedence.ADDITIVE, DefaultOperators.SUBTRACT);

        register(Precedence.MULTIPLICATIVE, DefaultOperators.MULTIPLY);
        register(Precedence.MULTIPLICATIVE, DefaultOperators.DIVIDE);
        //register(Precedence.MULTIPLICATIVE, DefaultOperators.PERCENTAGE);
        register(Precedence.MULTIPLICATIVE, DefaultOperators.REMAINDER);
        //register(Precedence.MULTIPLICATIVE, DefaultOperators.MODULUS);
        //register(Precedence.MULTIPLICATIVE, DefaultOperators.N_ROOT);

        register(Precedence.EQUALITY, DefaultOperators.EQUALS);
        register(Precedence.EQUALITY, DefaultOperators.NOT_EQUALS);
//
        register(Precedence.RELATIONAL, DefaultOperators.GREATER_OR_EQUAL);
        register(Precedence.RELATIONAL, DefaultOperators.GREATER_THAN);
        register(Precedence.RELATIONAL, DefaultOperators.LESSER_OR_EQUAL);
        register(Precedence.RELATIONAL, DefaultOperators.LESSER_THAN);
//
//        register(Precedence.SHIFT, DefaultOperators.ZERO_FILL_RIGHT_SHIFT);
//        register(Precedence.SHIFT, DefaultOperators.RIGHT_SHIFT);
//        register(Precedence.SHIFT, DefaultOperators.LEFT_SHIFT);
//
////        register(Precedence.UNARY, DefaultOperators.PRE_INCREMENT);
////        register(Precedence.UNARY, DefaultOperators.PRE_DECREMENT);
        register(Precedence.UNARY, DefaultOperators.UNARY_PLUS);
        register(Precedence.UNARY, DefaultOperators.UNARY_MINUS);
//        register(Precedence.UNARY, DefaultOperators.BITWISE_COMPLEMENT);
//        register(Precedence.UNARY, DefaultOperators.LOGICAL_NOT);
//
//        register(Precedence.EXPONENTIAL, DefaultOperators.EXPONENTATION);
//        register(Precedence.EXPONENTIAL, DefaultOperators.SCIENTIFIC_EX);
//
////        register(Precedence.POSTFIX, DefaultOperators.POST_INCREMENT);
////        register(Precedence.POSTFIX, DefaultOperators.POST_DECREMENT);
//
//        register(Precedence.POSTFIX, DefaultOperators.DEGREES);
//        register(Precedence.POSTFIX, DefaultOperators.LIST_INDEX);
    }

}
