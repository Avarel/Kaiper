package xyz.hexav.aje.operators;

import java.util.*;

public class OperatorMap {
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

    public OperatorMap copy() {
        return new OperatorMap(this);
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
