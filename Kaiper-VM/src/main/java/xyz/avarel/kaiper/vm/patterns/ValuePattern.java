package xyz.avarel.kaiper.vm.patterns;


import xyz.avarel.kaiper.ast.Expr;

// literally literals
public class ValuePattern implements Pattern {
    private final Expr value;

    public ValuePattern(Expr value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public Expr getValue() {
        return value;
    }
}
