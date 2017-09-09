package xyz.avarel.kaiper.runtime.pattern;

// x
public class VariableRuntimeLibPattern extends NamedRuntimeLibPattern {
    public VariableRuntimeLibPattern(String name) {
        super(name);
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return getName();
    }
}
