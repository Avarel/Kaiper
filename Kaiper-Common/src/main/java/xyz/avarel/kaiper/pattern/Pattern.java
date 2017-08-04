package xyz.avarel.kaiper.pattern;

public interface Pattern {
    <R, C> R accept(PatternVisitor<R, C> visitor, C scope);
}
