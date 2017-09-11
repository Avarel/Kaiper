package xyz.avarel.kaiper.vm.patterns;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// multiple of patterns
public class PatternCase implements Pattern {
    public static final PatternCase EMPTY = new PatternCase(Collections.emptyList());

    private final List<Pattern> patterns;

    public PatternCase(Pattern... patterns) {
        this(Arrays.asList(patterns));
    }

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

    public int size() {
        return patterns.size();
    }
}
