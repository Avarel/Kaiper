package xyz.avarel.aje;

import xyz.avarel.aje.pool.DefaultPool;
import xyz.avarel.aje.types.Any;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
    private final List<String> lines = new ArrayList<>();

    private Map<String, Any> objects;

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

    public Map<String, Any> getObjects() {
        return objects != null ? objects : (objects = DefaultPool.copy());
    }

    public T setObjects(Map<String, Any> objects) {
        this.objects = objects;
        return (T) this;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getScript() {
        return lines.stream().collect(Collectors.joining(";"));
    }
}
