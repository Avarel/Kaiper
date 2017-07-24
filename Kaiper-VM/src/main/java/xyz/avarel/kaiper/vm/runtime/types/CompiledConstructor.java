package xyz.avarel.kaiper.vm.runtime.types;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Undefined;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.runtime.types.Constructor;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.compiled.CompiledExecution;
import xyz.avarel.kaiper.vm.runtime.functions.CompiledParameter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class CompiledConstructor extends Constructor {
    private final List<CompiledParameter> parameters;
    private final List<Obj> superParameters;
    private final Scope scope;
    private final CompiledExecution executor;

    public CompiledConstructor(List<CompiledParameter> parameters, List<Obj> superParameters, CompiledExecution executor, Scope scope) {
        this.parameters = parameters;
        this.superParameters = superParameters;
        this.executor = executor;
        this.scope = scope;
    }

    @Override
    public List<CompiledParameter> getParameters() {
        return parameters;
    }

    @Override
    public CompiledObj invoke(List<Obj> arguments) {
        return eval(arguments, this.scope);
    }

    private CompiledObj eval(List<Obj> arguments, Scope scope) {
        try {
            if (!(targetType instanceof CompiledType)) {
                throw new ComputeException("Internal error");
            }

            Scope constructorScope = scope.subPool();

            for (int i = 0; i < getArity(); i++) {
                CompiledParameter parameter = parameters.get(i);

                if (i < arguments.size()) {
                    constructorScope.declare(parameter.getName(), arguments.get(i));
                } else if (parameter.hasDefault()) {
                    constructorScope.declare(parameter.getName(), parameter.getDefaultValue().executeWithScope(scope));
                } else {
                    constructorScope.declare(parameter.getName(), Undefined.VALUE);
                }
            }


            if (!parameters.isEmpty()) {
                Parameter lastParam = parameters.get(parameters.size() - 1);

                if (lastParam.isRest()) {
                    if (arguments.size() > getArity()) {
                        List<Obj> sublist = arguments.subList(parameters.size() - 1, arguments.size());
                        constructorScope.declare(lastParam.getName(), Array.ofList(sublist));
                    } else {
                        constructorScope.declare(lastParam.getName(), new Array());
                    }
                }
            }

            CompiledObj parent = null;

            if (targetType.hasParent()) {
                List<Obj> superArguments = new ArrayList<>(superParameters);

                if (targetType.getParent() != Obj.TYPE) {
                    if (!(targetType.getParent() instanceof CompiledType)) {
                        throw new ComputeException(targetType.getParent() + " can not be extended");
                    }

                    CompiledConstructor parentConstructor = ((CompiledType) targetType.getParent()).getConstructor();

                    parent = parentConstructor.invoke(superArguments);
                    scope = scope.combine(parent.getScope());

                    constructorScope.declare("super", parent);
                }
            }

            CompiledObj instance = new CompiledObj((CompiledType) targetType, parent, scope.subPool());

            constructorScope.declare("this", instance);

            executor.executeWithScope(constructorScope);

            return instance;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}