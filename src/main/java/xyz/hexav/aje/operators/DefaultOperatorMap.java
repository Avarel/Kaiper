package xyz.hexav.aje.operators;

import xyz.hexav.aje.defaults.DefaultOperators;

public class DefaultOperatorMap extends OperatorMap {
    public static final OperatorMap INSTANCE = new DefaultOperatorMap();
    
    private DefaultOperatorMap() {
        register(Precedence.ASSIGNMENT, DefaultOperators.VAR_ASSIGNMENT.get());

        register(Precedence.LOGICAL_OR, DefaultOperators.LOGICAL_OR.get());
        register(Precedence.LOGICAL_AND, DefaultOperators.LOGICAL_AND.get());

        register(Precedence.ADDITIVE, DefaultOperators.ADD.get());
        register(Precedence.ADDITIVE, DefaultOperators.SUBTRACT.get());

        register(Precedence.MULTIPLICATIVE, DefaultOperators.MULTIPLY.get());
        register(Precedence.MULTIPLICATIVE, DefaultOperators.DIVIDE.get());
        register(Precedence.MULTIPLICATIVE, DefaultOperators.REMAINDER.get());
        register(Precedence.MULTIPLICATIVE, DefaultOperators.MODULUS.get());
        register(Precedence.MULTIPLICATIVE, DefaultOperators.PERCENTAGE.get());
        register(Precedence.MULTIPLICATIVE, DefaultOperators.N_ROOT.get());

        register(Precedence.EQUALITY, DefaultOperators.EQUALS.get());
        register(Precedence.EQUALITY, DefaultOperators.NOT_EQUALS.get());

        register(Precedence.RELATIONAL, DefaultOperators.GREATER_OR_EQUAL.get());
        register(Precedence.RELATIONAL, DefaultOperators.GREATER_THAN.get());
        register(Precedence.RELATIONAL, DefaultOperators.LESSER_OR_EQUAL.get());
        register(Precedence.RELATIONAL, DefaultOperators.LESSER_THAN.get());

        register(Precedence.SHIFT, DefaultOperators.ZERO_FILL_RIGHT_SHIFT.get());
        register(Precedence.SHIFT, DefaultOperators.RIGHT_SHIFT.get());
        register(Precedence.SHIFT, DefaultOperators.LEFT_SHIFT.get());

//        register(Precedence.UNARY, DefaultOperators.PRE_INCREMENT.get());
//        register(Precedence.UNARY, DefaultOperators.PRE_DECREMENT.get());
        register(Precedence.UNARY, DefaultOperators.UNARY_PLUS.get());
        register(Precedence.UNARY, DefaultOperators.UNARY_MINUS.get());
        register(Precedence.UNARY, DefaultOperators.BITWISE_COMPLEMENT.get());
        register(Precedence.UNARY, DefaultOperators.LOGICAL_NOT.get());

        register(Precedence.EXPONENTIAL, DefaultOperators.EXPONENTATION.get());
        register(Precedence.EXPONENTIAL, DefaultOperators.SCIENTIFIC_EX.get());

//        register(Precedence.POSTFIX, DefaultOperators.POST_INCREMENT.get());
//        register(Precedence.POSTFIX, DefaultOperators.POST_DECREMENT.get());

        register(Precedence.POSTFIX, DefaultOperators.DEGREES.get());
        register(Precedence.POSTFIX, DefaultOperators.ITEM_AT_LIST.get());
    }

}
