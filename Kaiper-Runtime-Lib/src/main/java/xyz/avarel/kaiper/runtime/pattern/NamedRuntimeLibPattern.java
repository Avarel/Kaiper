package xyz.avarel.kaiper.runtime.pattern;

public abstract class NamedRuntimeLibPattern implements RuntimeLibPattern {
    private final String name;

    public NamedRuntimeLibPattern(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
