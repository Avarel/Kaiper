package xyz.hexav.aje;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionBuilder extends AbstractBuilder<FunctionBuilder> {
    private String name;
    private List<String> parameters;

    public FunctionBuilder(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public FunctionBuilder(String name, String script) {
        this(name);
        addLine(script);
    }

    public FunctionBuilder addParameter(String param) {
        this.parameters.add(param);
        return this;
    }

    public FunctionBuilder addParameter(String... parameters) {
        return addParameter(Arrays.asList(parameters));
    }

    public FunctionBuilder addParameter(List<String> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public MathFunction build() {
        return new MathFunction(name, getScript(), parameters, getPool());
    }
}
