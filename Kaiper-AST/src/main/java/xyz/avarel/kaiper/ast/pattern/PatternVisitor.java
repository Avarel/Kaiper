package xyz.avarel.kaiper.ast.pattern;

public interface PatternVisitor<R, C> {
    R visit(PatternCase patternCase, C scope);
    R visit(WildcardPattern pattern, C context);
    R visit(VariablePattern pattern, C context);
    R visit(TuplePattern pattern, C context);
    R visit(RestPattern pattern, C context);
    R visit(ValuePattern pattern, C context);
    R visit(DefaultPattern pattern, C context);
}
