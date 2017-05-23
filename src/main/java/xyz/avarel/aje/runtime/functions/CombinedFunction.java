package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CombinedFunction extends AJEFunction {
    private final AJEFunction left;
    private final AJEFunction right;
    private final BinaryOperator<Obj> operator;

    public CombinedFunction(AJEFunction left, AJEFunction right, BinaryOperator<Obj> operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        return "combine(" + left + ", " + right + ")";
    }

    @Override
    public Obj invoke(List<Obj> args) {
        if (args.size() != getArity()) {
            return Undefined.VALUE;
        }

        return operator.apply(left.invoke(args), right.invoke(args));
    }
}
