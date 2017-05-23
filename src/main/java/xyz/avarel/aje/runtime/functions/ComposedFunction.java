package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class ComposedFunction extends AJEFunction {
    private final AJEFunction left;
    private final AJEFunction right;

    public ComposedFunction(AJEFunction left, AJEFunction right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        return "compose(" + left + ", " + right + ")";
    }

    @Override
    public Obj invoke(List<Obj> args) {
        if (args.size() != getArity()) {
            return Undefined.VALUE;
        }

        return left.invoke(right.invoke(args));
    }
}
