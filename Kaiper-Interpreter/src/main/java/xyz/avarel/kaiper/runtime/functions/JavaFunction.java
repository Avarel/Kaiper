package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.ast.pattern.PatternBinder;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.util.Map;
import java.util.TreeMap;

public class JavaFunction extends Function {
    private final Map<PatternCase, Transformer> dispatches;
    private final ExprInterpreter interpreter;

    public JavaFunction(String name, ExprInterpreter interpreter) {
        super(name);
        this.interpreter = interpreter;
        this.dispatches = new TreeMap<>();
    }

    public JavaFunction addDispatch(PatternCase patternCase, Transformer expr) {
        dispatches.put(patternCase, expr);
        return this;
    }

    @Override
    public Obj invoke(Obj argument) {
        for (Map.Entry<PatternCase, Transformer> entry : dispatches.entrySet()) {
            Scope<String, Obj> subScope = new Scope<>();

            if (new PatternBinder(interpreter, subScope).bind(entry.getKey(), argument)) {
                return entry.getValue().invoke(subScope);
            }
        }

        throw new InterpreterException("No method matches for " + argument);
    }

    @FunctionalInterface
    public interface Transformer {
        Obj invoke(Scope<String, Obj> scope);
    }
}
