package xyz.avarel.kaiper.runtime.pattern;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// multiple of patterns
public class RuntimeLibPatternCase implements RuntimeLibPattern {
    public static final RuntimeLibPatternCase EMPTY = new RuntimeLibPatternCase(Collections.emptyList());

    private final List<RuntimeLibPattern> patterns;

    public RuntimeLibPatternCase(RuntimeLibPattern... patterns) {
        this(Arrays.asList(patterns));
    }

    public RuntimeLibPatternCase(List<RuntimeLibPattern> patterns) {
        this.patterns = patterns;
    }

    public List<RuntimeLibPattern> getPatterns() {
        return patterns;
    }

    public String toString() {
        if (patterns.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<RuntimeLibPattern> iterator = patterns.iterator();

        while (true) {
            RuntimeLibPattern pattern = iterator.next();
            sb.append(pattern);
            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }

        return sb.toString();
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    public int size() {
        return patterns.size();
    }
}
