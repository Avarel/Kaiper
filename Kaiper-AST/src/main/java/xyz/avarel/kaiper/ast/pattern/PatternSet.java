package xyz.avarel.kaiper.ast.pattern;

import java.util.Iterator;
import java.util.List;

public class PatternSet {
    private final List<Pattern> patterns;

    public PatternSet(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public String toString() {
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
}
