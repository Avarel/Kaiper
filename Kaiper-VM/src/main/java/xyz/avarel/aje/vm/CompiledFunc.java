package xyz.avarel.aje.vm;

import xyz.avarel.aje.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.aje.exceptions.ReturnException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.scope.Scope;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;

public class CompiledFunc extends Func {
    private final LinkedList<Parameter> parameters;
    private final byte[] bytecode;
    private final BytecodeBatchReader reader;
    private final Scope scope;
    private final List<String> stringPool;
    private final int depth;

    public CompiledFunc(String name, LinkedList<Parameter> parameters, byte[] bytecode, Scope scope, BytecodeBatchReader reader, List<String> stringPool, int depth) {
        super(name);
        this.parameters = parameters;
        this.bytecode = bytecode;
        this.reader = reader;
        this.scope = scope;
        this.stringPool = stringPool;
        this.depth = depth;
    }

    @Override
    public int getArity() {
        if (parameters.isEmpty()) return 0;
        return parameters.get(parameters.size() - 1).isRest() ? parameters.size() - 1 : parameters.size();
    }

    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        Scope scope = this.scope.subPool();
        for (int i = 0; i < getArity(); i++) {
            Parameter parameter = parameters.get(i);

            if (i < arguments.size()) {
                scope.declare(parameter.getName(), arguments.get(i));
            } else if (parameter.hasDefault()) {
                scope.declare(parameter.getName(), parameter.getDefault());
            } else {
                scope.declare(parameter.getName(), Undefined.VALUE);
            }
        }

        if (!parameters.isEmpty()) {
            Parameter lastParam = parameters.get(parameters.size() - 1);

            if (lastParam.isRest()) {
                if (arguments.size() > getArity()) {
                    List<Obj> sublist = arguments.subList(parameters.size() - 1, arguments.size());
                    scope.declare(lastParam.getName(), Array.ofList(sublist));
                } else {
                    scope.declare(lastParam.getName(), new Array());
                }
            }
        }

        try {
            StackMachineWalker walker = new StackMachineWalker(scope);
            reader.walkInsts(new DataInputStream(new ByteArrayInputStream(bytecode)), walker, stringPool, depth);
            return walker.stack.pop();
        } catch (IOException e) {
            throw new UncheckedIOException(e); //this shouldn't happen but okay so
        } catch (ReturnException re) {
            return re.getValue();
        }
    }
}
