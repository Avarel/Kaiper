package xyz.avarel.kaiper.runtime.types;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.pattern.PatternBinder;
import xyz.avarel.kaiper.pattern.PatternCase;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;

public class CompiledConstructor extends Constructor {
    private final PatternCase pattern;
    private final ExprInterpreter visitor;
    private final Scope scope;
    private final Expr expr;

    public CompiledConstructor(PatternCase pattern, Expr expr, ExprInterpreter visitor, Scope scope) {
        this.pattern = pattern;
        this.visitor = visitor;
        this.scope = scope;
        this.expr = expr;
    }

    @Override
    public PatternCase getPattern() {
        return pattern;
    }

    @Override
    public CompiledObj invoke(Tuple arguments) {
        if (!(targetType instanceof CompiledType)) {
            throw new ComputeException("Internal error");
        }

        Scope constructorScope = this.scope.subPool();

        if (!new PatternBinder(pattern, visitor, constructorScope).declareFrom(arguments)) {
            throw new InterpreterException("Could not match arguments (" + arguments + ") to " + getName() + "(" + pattern + ")");
        }

        CompiledObj instance = new CompiledObj((CompiledType) targetType, constructorScope);

        constructorScope.declare("this", instance);

        expr.accept(visitor, constructorScope);

        return instance;
    }

}