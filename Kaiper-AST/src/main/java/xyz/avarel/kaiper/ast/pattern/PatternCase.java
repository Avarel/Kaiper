package xyz.avarel.kaiper.ast.pattern;

import java.util.Iterator;
import java.util.List;

public class PatternCase extends Pattern {
    private final List<Pattern> patterns;

    public PatternCase(List<Pattern> patterns) {
        super(patterns.get(0).getPosition());
        this.patterns = patterns;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
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
        sb.append(")");

        return sb.toString();
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }
}
