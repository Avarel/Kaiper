package xyz.avarel.kaiper.ast.pattern;

public interface PatternVisitor<R, C> {
    R accept(WildcardPattern pattern, C scope);
    R accept(ValuePattern pattern, C scope);
    R accept(TypePattern pattern, C scope);
    R accept(VariablePattern pattern, C scope);
    R accept(DefaultPattern pattern, C scope);
    R accept(TuplePattern pattern, C scope);
}
