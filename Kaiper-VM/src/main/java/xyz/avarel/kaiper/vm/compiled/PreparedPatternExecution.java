package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;
import xyz.avarel.kaiper.vm.states.StatelessStackMachines;
import xyz.avarel.kaiper.vm.states.VMState;

import java.io.ByteArrayInputStream;

public class PreparedPatternExecution {
    private final byte[] bytecode;
    private final String[] stringPool;

    public PreparedPatternExecution(byte[] bytecode, String[] stringPool) {
        this.bytecode = bytecode;
        this.stringPool = stringPool;
    }


    public boolean executeAssign(Scope scope, Tuple tuple) {
        StackMachine machine = StatelessStackMachines.borrow();
        machine.stringPool = stringPool;
        machine.scope = scope;

        try {
            return machine.patternProcessor.assignFrom(tuple, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            StatelessStackMachines.release(machine);
        }
    }

    public boolean executeDeclare(Scope scope, Tuple tuple) {
        StackMachine machine = StatelessStackMachines.borrow();
        machine.stringPool = stringPool;
        machine.scope = scope;

        try {
            return machine.patternProcessor.declareFrom(tuple, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            StatelessStackMachines.release(machine);
        }
    }

    public boolean executeAssign(StackMachine machine, Scope scope, Tuple tuple) {
        if (machine == null) {
            return executeAssign(scope, tuple);
        }

        VMState state = VMState.save(machine);

        //load temp state
        machine.scope = scope;
        machine.stringPool = stringPool;

        try {
            return machine.patternProcessor.assignFrom(tuple, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            state.load(machine);
        }
    }

    public boolean executeDeclare(StackMachine machine, Scope scope, Tuple tuple) {
        if (machine == null) {
            return executeAssign(scope, tuple);
        }

        VMState state = VMState.save(machine);

        //load temp state
        machine.scope = scope;
        machine.stringPool = stringPool;

        try {
            return machine.patternProcessor.declareFrom(tuple, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            state.load(machine);
        }
    }
}
