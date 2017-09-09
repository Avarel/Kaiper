package xyz.avarel.kaiper.ast.pattern;


import xyz.avarel.kaiper.ast.Expr;

// literally literals
public class ValuePattern implements Pattern {
    private final Expr value;

    public ValuePattern(Expr value) {
        this.value = value;
    }

    public Expr getValue() {
        return value;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
