package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;

import static xyz.avarel.kaiper.bytecode.pattern.PatternOpcodes.*;

public class PatternCompiler implements PatternVisitor<Void, ByteOutput> {
    private final ExprCompiler parent;

    public PatternCompiler(ExprCompiler parent) {
        this.parent = parent;
    }

    @Override
    public Void visit(PatternCase patternCase, ByteOutput out) {
        out.write(PATTERN_CASE);

        int id = parent.regionId;
        parent.regionId++;

        for (Pattern pattern : patternCase.getPatterns()) {
            pattern.accept(this, out);
        }

        END.writeInto(out);
        out.writeShort(id);

        parent.regionId--;

        return null;
    }

    @Override
    public Void visit(WildcardPattern pattern, ByteOutput out) {
        out.write(WILDCARD);

        return null;
    }

    @Override
    public Void visit(VariablePattern pattern, ByteOutput out) {
        out.write(VARIABLE);
        out.writeShort(parent.stringConst(pattern.getName()));

        return null;
    }

    @Override
    public Void visit(TuplePattern pattern, ByteOutput out) {
        int name = parent.stringConst(pattern.getName());

        out.write(TUPLE);
        out.writeShort(name);

        int id = parent.regionId;
        parent.regionId++;

        pattern.getPattern().accept(this, out);
        END.writeInto(out);
        out.writeShort(id);

        parent.regionId--;

        return null;
    }

    @Override
    public Void visit(RestPattern pattern, ByteOutput out) {
        out.write(REST);
        out.writeShort(parent.stringConst(pattern.getName()));

        return null;
    }

    @Override
    public Void visit(ValuePattern pattern, ByteOutput out) {
        out.write(VALUE);

        int id = parent.regionId;
        parent.regionId++;

        pattern.getValue().accept(parent, out);
        END.writeInto(out);
        out.writeShort(id);

        parent.regionId--;

        return null;
    }

    @Override
    public Void visit(DefaultPattern pattern, ByteOutput out) {
        out.write(DEFAULT);

        int id = parent.regionId;
        parent.regionId++;

        pattern.getDelegate().accept(this, out);
        END.writeInto(out);
        out.writeShort(id);

        pattern.getDefault().accept(parent, out);
        END.writeInto(out);
        out.writeShort(id);

        parent.regionId--;

        return null;
    }
}
