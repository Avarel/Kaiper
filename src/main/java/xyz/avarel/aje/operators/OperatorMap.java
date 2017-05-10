package xyz.avarel.aje.operators;

import java.util.*;

public class OperatorMap {
    private Map<Integer, Set<AJEUnaryOperator>> unaryOperators;
    private Map<Integer, Set<AJEBinaryOperator>> binaryOperators;
    private Map<Integer, Set<AJEUnaryOperator>> postfixOperators;

    public OperatorMap() {
        this(new TreeMap<>(), new TreeMap<>());
    }

    public OperatorMap(OperatorMap toCopy) {
        this(new TreeMap<>(toCopy.unaryOperators), new TreeMap<>(toCopy.binaryOperators));
    }

    public OperatorMap(Map<Integer, Set<AJEUnaryOperator>> unaryOperators, Map<Integer, Set<AJEBinaryOperator>> binaryOperators) {
        this.unaryOperators = unaryOperators;
        this.binaryOperators = binaryOperators;
    }

    public OperatorMap copy() {
        return new OperatorMap(this);
    }

    public void registerPrefix(int precedence, AJEUnaryOperator operator) {
        if (!unaryOperators.containsKey(precedence)) {
            unaryOperators.put(precedence, new LinkedHashSet<>());
        }
        unaryOperators.get(precedence).add(operator);
    }

    public void registerBinary(int precedence, AJEBinaryOperator operator) {
        if (!binaryOperators.containsKey(precedence)) {
            binaryOperators.put(precedence, new LinkedHashSet<>());
        }
        binaryOperators.get(precedence).add(operator);
    }

    public Map<Integer, Set<AJEBinaryOperator>> binary() {
        return binaryOperators;
    }

    public int after(int precedence) {
        boolean flag = false;
        for (int i : binaryOperators.keySet()) {
            if (flag) return i;
            if (i == precedence) flag = true;
        }
        return -1;


    }

    public int firstPrecedence() {
        return new ArrayList<>(binaryOperators.keySet()).get(0);
    }

    public int lastPrecedence() {
        int last = 0;
        for (int i : binaryOperators.keySet()) {
            if (i > last) last = i;
        }
        return last;
    }

    public Map<Integer, Set<AJEUnaryOperator>> prefix() {
        return unaryOperators;
    }
}
