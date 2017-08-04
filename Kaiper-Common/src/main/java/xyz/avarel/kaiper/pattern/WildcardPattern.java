package xyz.avarel.kaiper.pattern;

// _
public enum WildcardPattern implements Pattern {
    INSTANCE;

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "_";
    }
}
