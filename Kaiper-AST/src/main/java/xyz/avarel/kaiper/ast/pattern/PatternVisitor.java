package xyz.avarel.kaiper.ast.pattern;

public interface PatternVisitor<R, C> {
//    R accept(PatternCase patternCase, C scope);
    R accept(WildcardPattern pattern, C context);
    R accept(ValuePattern pattern, C context);
    R accept(VariablePattern pattern, C context);
    R accept(DefaultPattern pattern, C context);
    R accept(TuplePattern pattern, C context);
    R accept(RestPattern pattern, C context);
}
