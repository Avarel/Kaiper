package xyz.avarel.kaiper.runtime.pattern;

// a: is Int
// a: 2
// a: x
// a: (2, meme: 2, dank: 3)
public class TupleRuntimeLibPattern extends NamedRuntimeLibPattern {
    private final RuntimeLibPattern pattern;

    public TupleRuntimeLibPattern(String name, RuntimeLibPattern pattern) {
        super(name);
        this.pattern = pattern;
    }

    public RuntimeLibPattern getPattern() {
        return pattern;
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return getName() + ": " + pattern;
    }
}
