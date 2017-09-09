package xyz.avarel.kaiper.ast.pattern;

public abstract class NamedPattern implements Pattern {
    private final String name;

    public NamedPattern(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
