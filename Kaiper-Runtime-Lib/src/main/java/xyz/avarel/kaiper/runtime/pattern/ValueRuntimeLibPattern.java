package xyz.avarel.kaiper.runtime.pattern;


import xyz.avarel.kaiper.runtime.Obj;

// literally literals
public class ValueRuntimeLibPattern implements RuntimeLibPattern {
    private final Obj value;

    public ValueRuntimeLibPattern(Obj value) {
        this.value = value;
    }

    public Obj getValue() {
        return value;
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
