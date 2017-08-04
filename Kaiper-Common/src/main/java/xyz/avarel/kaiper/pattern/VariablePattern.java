package xyz.avarel.kaiper.pattern;

// x
public class VariablePattern extends NamedPattern  {
    public VariablePattern(String name) {
        super(name);
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return getName();
    }
}
