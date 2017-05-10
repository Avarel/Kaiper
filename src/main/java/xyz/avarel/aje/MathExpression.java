package xyz.avarel.aje;

import xyz.avarel.aje.types.Any;

import java.util.List;
import java.util.Map;

public class MathExpression {
    private final String script;
    private Any expression;

    private final Map<String, Any> objects;

    protected MathExpression(String scripts, Map<String, Any> objects) {
        this.script = scripts;
        this.objects = objects;
    }

    protected MathExpression(String scripts, List<String> variables, Map<String, Any> objects) {
        this(scripts, objects);

        if (variables != null) {
            for (String name : variables) {
//                getMap<String, Any>().allocVar(name);
            }
        }
    }

    public MathExpression compile() {
        if (expression == null) {
            expression = new AJEParser(objects, script).compute();
        }
        return this;
    }

    @Deprecated()
    public MathExpression forceCompile() {
        expression = new AJEParser(objects, script).compute();
        return this;
    }
//
//    public MathExpression setVariable(String name, double value) {
//        pool.getVar(name).assign(value);
//        return this;
//    }

//    public MathExpression setVariable(String name, Expression value) {
//        pool.getVar(name).assign(value);
//        return this;
//    }

    public Any eval() {
        compile();
        return expression;
    }
//
//    public double numValue() {
//        compile();
//        return ((Operable) expression).value();
//    }
//
//    @Override
//    public String asString() {
//        eval();
//        return expression.asString();
//    }
//
//    public Expression getExpression() {
//        return expression;
//    }

    public Map<String, Any> getObjects() {
        return objects;
    }
}
