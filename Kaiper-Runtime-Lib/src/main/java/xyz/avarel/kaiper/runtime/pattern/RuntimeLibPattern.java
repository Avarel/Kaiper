package xyz.avarel.kaiper.runtime.pattern;

public interface RuntimeLibPattern {
    <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope);
}
