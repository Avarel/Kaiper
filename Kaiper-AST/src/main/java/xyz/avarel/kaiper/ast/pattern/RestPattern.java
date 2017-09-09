package xyz.avarel.kaiper.ast.pattern;

public class RestPattern extends NamedPattern {
    public RestPattern(String name) {
        super(name);
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "..." + getName();
    }
}
