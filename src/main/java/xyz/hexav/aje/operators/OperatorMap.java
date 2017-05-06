package xyz.hexav.aje.operators;

import java.util.*;

public class OperatorMap {
    private List<AJEUnaryOperator> unaryOperators;
    private Map<Integer, Set<AJEBinaryOperator>> operators;

    public OperatorMap() {
        this(new ArrayList<>(), new TreeMap<>());
    }

    public OperatorMap(OperatorMap toCopy) {
        this(new ArrayList<>(toCopy.unaryOperators), new TreeMap<>(toCopy.operators));
    }

    public OperatorMap(List<AJEUnaryOperator> unaryOperators, Map<Integer, Set<AJEBinaryOperator>> operators) {
        this.unaryOperators = unaryOperators;
        this.operators = operators;
    }

    public OperatorMap copy() {
        return new OperatorMap(this);
    }

    public void register(AJEUnaryOperator operator) {
        unaryOperators.add(operator);
    }

    public void register(int precedence, AJEBinaryOperator operator) {
        if (!operators.containsKey(precedence)) {
            operators.put(precedence, new LinkedHashSet<>());
        }
        operators.get(precedence).add(operator);
    }

    public Set<AJEBinaryOperator> get(int precedence) {
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

    public List<AJEUnaryOperator> getUnaryOperators() {
        return unaryOperators;
    }
}
