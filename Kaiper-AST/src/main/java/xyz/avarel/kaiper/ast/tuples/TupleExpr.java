package xyz.avarel.kaiper.ast.tuples;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TupleExpr extends Single {
    private final List<Single> unnamedElements;
    private final Map<String, Single> namedElements;

    public TupleExpr(Position position, Single element) {
        this(position, Collections.singletonList(element), Collections.emptyMap());
    }

    public TupleExpr(Position position, String name, Single element) {
        this(position, Collections.emptyList(), Collections.singletonMap(name, element));
    }

    public TupleExpr(Position position, List<Single> unnamedElements, Map<String, Single> namedElements) {
        super(position);
        this.unnamedElements = unnamedElements;
        this.namedElements = namedElements;
    }

    public List<Single> getUnnamedElements() {
        return unnamedElements;
    }

    public Map<String, Single> getNamedElements() {
        return namedElements;
    }

    public int size() {
        return unnamedElements.size() + namedElements.size();
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return unnamedElements.toString() + namedElements.toString();
    }
}
