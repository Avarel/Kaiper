package xyz.avarel.kaiper.runtime.pattern;

// _
public enum WildcardRuntimeLibPattern implements RuntimeLibPattern {
    INSTANCE;

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "_";
    }
}
