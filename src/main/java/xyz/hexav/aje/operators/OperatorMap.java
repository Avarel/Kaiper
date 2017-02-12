package xyz.hexav.aje.operators;

import xyz.hexav.aje.defaults.DefaultOperators;

import java.util.*;

public class OperatorMap {
    private static final OperatorMap defaultOperators = new OperatorMap();

    static {
        defaultOperators.register(Precedence.ASSIGNMENT, DefaultOperators.VAR_ASSIGNMENT.get());

        defaultOperators.register(Precedence.LOGICAL_OR, DefaultOperators.LOGICAL_OR.get());
        defaultOperators.register(Precedence.LOGICAL_AND, DefaultOperators.LOGICAL_AND.get());

        defaultOperators.register(Precedence.ADDITIVE, DefaultOperators.ADD.get());
        defaultOperators.register(Precedence.ADDITIVE, DefaultOperators.SUBTRACT.get());

        defaultOperators.register(Precedence.MULTIPLICATIVE, DefaultOperators.MULTIPLY.get());
        defaultOperators.register(Precedence.MULTIPLICATIVE, DefaultOperators.DIVIDE.get());
        defaultOperators.register(Precedence.MULTIPLICATIVE, DefaultOperators.REMAINDER.get());
        defaultOperators.register(Precedence.MULTIPLICATIVE, DefaultOperators.MODULUS.get());
        defaultOperators.register(Precedence.MULTIPLICATIVE, DefaultOperators.PERCENTAGE.get());
        defaultOperators.register(Precedence.MULTIPLICATIVE, DefaultOperators.N_ROOT.get());

        defaultOperators.register(Precedence.EQUALITY, DefaultOperators.EQUALS.get());
        defaultOperators.register(Precedence.EQUALITY, DefaultOperators.NOT_EQUALS.get());

        defaultOperators.register(Precedence.RELATIONAL, DefaultOperators.GREATER_OR_EQUAL.get());
        defaultOperators.register(Precedence.RELATIONAL, DefaultOperators.GREATER_THAN.get());
        defaultOperators.register(Precedence.RELATIONAL, DefaultOperators.LESSER_OR_EQUAL.get());
        defaultOperators.register(Precedence.RELATIONAL, DefaultOperators.LESSER_THAN.get());

        defaultOperators.register(Precedence.SHIFT, DefaultOperators.ZERO_FILL_RIGHT_SHIFT.get());
        defaultOperators.register(Precedence.SHIFT, DefaultOperators.RIGHT_SHIFT.get());
        defaultOperators.register(Precedence.SHIFT, DefaultOperators.LEFT_SHIFT.get());

        defaultOperators.register(Precedence.UNARY, DefaultOperators.PRE_INCREMENT.get());
        defaultOperators.register(Precedence.UNARY, DefaultOperators.PRE_DECREMENT.get());
        defaultOperators.register(Precedence.UNARY, DefaultOperators.UNARY_PLUS.get());
        defaultOperators.register(Precedence.UNARY, DefaultOperators.UNARY_MINUS.get());
        defaultOperators.register(Precedence.UNARY, DefaultOperators.BITWISE_COMPLEMENT.get());
        defaultOperators.register(Precedence.UNARY, DefaultOperators.LOGICAL_NOT.get());

        defaultOperators.register(Precedence.EXPONENTIAL, DefaultOperators.EXPONENTATION.get());
        defaultOperators.register(Precedence.EXPONENTIAL, DefaultOperators.SCIENTIFIC_EX.get());

        defaultOperators.register(Precedence.POSTFIX, DefaultOperators.POST_INCREMENT.get());
        defaultOperators.register(Precedence.POSTFIX, DefaultOperators.POST_DECREMENT.get());

        defaultOperators.register(Precedence.POSTFIX, DefaultOperators.DEGREES.get());
        defaultOperators.register(Precedence.POSTFIX, DefaultOperators.ITEM_AT_LIST.get());
    }

    private Map<Integer, Set<Operator>> operators;

    public OperatorMap() {
        this(new TreeMap<>());
    }

    public OperatorMap(OperatorMap toCopy) {
        this(new TreeMap<>(toCopy.operators));
    }

    public OperatorMap(Map<Integer, Set<Operator>> operators) {
        this.operators = operators;
    }

    public static OperatorMap getDefaultOperators() {
        return new OperatorMap(defaultOperators);
    }

    public void register(int precedence, Operator operator) {
        if (!operators.containsKey(precedence)) {
            operators.put(precedence, new LinkedHashSet<>());
        }
        operators.get(precedence).add(operator);
    }

    public Set<Operator> get(int precedence) {
        return operators.get(precedence);
    }

    public int after(int precedence) {
        boolean flag = false;
        for (int i : operators.keySet()) {
            if (flag) return i;
            if (i == precedence) flag = true;
        }
        return -1;
    }

    public int firstPrecedence() {
        return new ArrayList<>(operators.keySet()).get(0);
    }

    public int lastPrecedence() {
        int last = 0;
        for (int i : operators.keySet()) {
            if (i > last) last = i;
        }
        return last;
    }
}
