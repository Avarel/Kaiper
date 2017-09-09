package xyz.avarel.kaiper.runtime.pattern;

public class RestRuntimeLibPattern extends NamedRuntimeLibPattern {
    public RestRuntimeLibPattern(String name) {
        super(name);
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "..." + getName();
    }
}
