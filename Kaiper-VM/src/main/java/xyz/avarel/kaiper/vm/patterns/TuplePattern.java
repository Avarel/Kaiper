package xyz.avarel.kaiper.vm.patterns;

// a: is Int
// a: 2
// a: x
// a: (2, meme: 2, dank: 3)
public class TuplePattern extends NamedPattern {
    private final Pattern pattern;

    public TuplePattern(String name, Pattern pattern) {
        super(name);
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return getName() + ": " + pattern;
    }
}
