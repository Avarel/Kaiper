package xyz.avarel.kaiper.vm.patterns;

public abstract class NamedPattern implements Pattern {
    private final String name;

    public NamedPattern(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
