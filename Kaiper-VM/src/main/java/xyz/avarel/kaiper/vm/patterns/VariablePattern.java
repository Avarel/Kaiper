package xyz.avarel.kaiper.vm.patterns;

// x
public class VariablePattern extends NamedPattern {
    public VariablePattern(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
