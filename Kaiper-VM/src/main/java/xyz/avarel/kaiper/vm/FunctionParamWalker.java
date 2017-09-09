package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.walker.BytecodeWalkerAdapter;
import xyz.avarel.kaiper.vm.runtime.functions.CompiledParameter;

import java.util.LinkedList;

class FunctionParamWalker extends BytecodeWalkerAdapter {
    LinkedList<CompiledParameter> parameters = new LinkedList<>();

    private StackMachineWalker parent;

    FunctionParamWalker(StackMachineWalker parent) {
        this.parent = parent;
    }

}
