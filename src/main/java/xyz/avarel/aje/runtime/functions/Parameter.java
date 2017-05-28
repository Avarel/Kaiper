package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;

public class Parameter {
    private final String name;
    private final Expr type;
    private final Expr defaultExpr;

    public Parameter(String name) {
        this(name, new ValueAtom(Obj.TYPE), null);
    }

    public Parameter(Type type) {
        this(null, new ValueAtom(type), null);
    }

    public Parameter(String name, Expr type) {
        this(name, type, null);
    }

    public Parameter(String name, Expr type, Expr defaultExpr) {
        this.name = name;
        this.type = type;
        this.defaultExpr = defaultExpr;
    }

    public String getName() {
        return name;
    }

    public Expr getType() {
        return type;
    }

    public boolean hasDefault() {
        return defaultExpr != null;
    }

    public Expr getDefault() {
        return defaultExpr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name);
        }
        if (!(type instanceof ValueAtom && type.compute() == Obj.TYPE)) {
            if (name != null) {
                sb.append(": ");
            }
            sb.append(type);
        }

        if (defaultExpr != null) {
            sb.append(" = ").append(defaultExpr);
        }
        return sb.toString();
    }
}
