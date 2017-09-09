package xyz.avarel.kaiper.runtime.pattern;

public interface RuntimePatternVisitor<R, C> {
    R visit(RuntimeLibPatternCase patternCase, C scope);
    R visit(WildcardRuntimeLibPattern pattern, C context);
    R visit(VariableRuntimeLibPattern pattern, C context);
    R visit(TupleRuntimeLibPattern pattern, C context);
    R visit(RestRuntimeLibPattern pattern, C context);
    R visit(ValueRuntimeLibPattern pattern, C context);
    R visit(DefaultRuntimeLibPattern pattern, C context);
}
