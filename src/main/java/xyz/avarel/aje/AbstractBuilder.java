package xyz.avarel.aje;

import xyz.avarel.aje.operators.AJEBinaryOperator;
import xyz.avarel.aje.operators.Precedence;
import xyz.avarel.aje.pool.DefaultPool;
import xyz.avarel.aje.pool.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
    private final List<String> lines = new ArrayList<>();

    private Pool pool;

    public T addLine(String script) {
        lines.add(script);
        return (T) this;
    }

    public T addLine(int i, String script) {
        lines.add(i, script);
        return (T) this;
    }

    public T removeLine(int i) {
        lines.remove(i);
        return (T) this;
    }

    public T addVariable(String variable) {
//        getPool().allocVar(variable);
        return (T) this;
    }

    public T addVariables(String... variables) {
        for (String var : variables) {
            addVariable(var);
        }
        return (T) this;
    }

    public T addVariables(List<String> variables) {
        for (String var : variables) {
            addVariable(var);
        }
        return (T) this;
    }

    public T addValue(String value) {
//        getPool().allocVar(value);
        return (T) this;
    }

    public T addValues(String... values) {
        for (String var : values) {
            addValue(var);
        }
        return (T) this;
    }

    public T addValues(List<String> values) {
        for (String var : values) {
            addValue(var);
        }
        return (T) this;
    }

    public T addOperator(AJEBinaryOperator operator) {
        return addOperator(Precedence.INFIX, operator);
    }

    public T addOperator(int precedence, AJEBinaryOperator operator) {
        getPool().getOperators().registerBinary(precedence, operator);
        return (T) this;
    }

    public Pool getPool() {
        return pool != null ? pool : (pool = DefaultPool.INSTANCE.copy());
    }

    public T setPool(Pool pool) {
        this.pool = pool;
        return (T) this;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getScript() {
        return lines.stream().collect(Collectors.joining(";"));
    }
}
