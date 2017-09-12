package xyz.avarel.kaiper.vm.states;

import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;

public class VMState {
    private static final VMState CLEAN_STATE = new VMState();

    public static void cleanState(StackMachine stackMachine) {
        CLEAN_STATE.loadState(stackMachine);
    }

    public static VMState saveState(StackMachine stackMachine) {
        return new VMState(stackMachine);
    }

    public static VMState saveAndCleanState(StackMachine stackMachine) {
        VMState state = saveState(stackMachine);
        cleanState(stackMachine);
        return state;
    }

    private final int stackSize;
    private final int stackLock;
    private final Scope scope;
    private final long lineNumber;

    private VMState() {
        stackSize = 0;
        stackLock = 0;
        scope = null;
        lineNumber = -1;
    }

    private VMState(StackMachine stackMachine) {
        stackSize = stackMachine.stack.size();
        stackLock = stackMachine.stack.getLock();
        scope = stackMachine.scope;
        lineNumber = stackMachine.lineNumber;
    }

    public VMState saveAndLoadState(StackMachine stackMachine) {
        VMState state = saveState(stackMachine);
        loadState(stackMachine);
        return state;
    }

    public void loadState(StackMachine stackMachine) {
        //Clear to size
        stackMachine.stack.setLock(stackSize);
        stackMachine.stack.popToLock();


        stackMachine.stack.setLock(stackLock);
        stackMachine.scope = scope;
        stackMachine.lineNumber = lineNumber;
    }
}
