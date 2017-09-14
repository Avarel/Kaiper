package xyz.avarel.kaiper.vm.states;

import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;

public class VMState {
    public static void cleanState(StackMachine stackMachine) {
        StatelessStackMachines.reset(stackMachine);
    }

    public static VMState save(StackMachine stackMachine) {
        return new VMState(stackMachine);
    }

    public static VMState saveAndCleanState(StackMachine stackMachine) {
        VMState state = save(stackMachine);
        cleanState(stackMachine);
        return state;
    }

    private final int stackLock;
    private final Scope scope;
    private final long lineNumber;
    private final String[] stringPool;

    private VMState() {
        stackLock = 0;
        scope = null;
        lineNumber = -1;
        stringPool = null;
    }

    private VMState(StackMachine stackMachine) {
        stackLock = stackMachine.stack.getLock();
        scope = stackMachine.scope;
        lineNumber = stackMachine.lineNumber;
        stringPool = stackMachine.stringPool;
    }

    public VMState saveAndLoadState(StackMachine stackMachine) {
        VMState state = save(stackMachine);
        load(stackMachine);
        return state;
    }


    public void load(StackMachine stackMachine) {
        stackMachine.stack.setLock(stackLock);
        stackMachine.scope = scope;
        stackMachine.lineNumber = lineNumber;
    }
}
