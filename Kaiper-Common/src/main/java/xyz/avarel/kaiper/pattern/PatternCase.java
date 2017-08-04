package xyz.avarel.kaiper.pattern;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// multiple of patterns
public class PatternCase implements Pattern {
    public static final PatternCase EMPTY_CASE = new PatternCase(Collections.emptyList());

    private final List<Pattern> patterns;

    public PatternCase(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public String toString() {
        if (patterns.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Pattern> iterator = patterns.iterator();

        while (true) {
            Pattern pattern = iterator.next();
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
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    public int size() {
        return patterns.size();
    }
}
