package xyz.avarel.kaiper.ast.pattern;

// _
public class WildcardPattern extends Pattern {
    public static final WildcardPattern INSTANCE = new WildcardPattern();

    private WildcardPattern() {
        super(null);
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "_";
    }
}
