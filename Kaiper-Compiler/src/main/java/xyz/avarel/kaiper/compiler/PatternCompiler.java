package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.bytecode.io.KDataOutput;

import static xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes.*;

public class PatternCompiler implements PatternVisitor<Void, KDataOutput> {
    private final ExprCompiler parent;

    public PatternCompiler(ExprCompiler parent) {
        this.parent = parent;
    }

    @Override
    public Void visit(PatternCase patternCase, KDataOutput out) {
        out.writeOpcode(PATTERN_CASE);

        int id = parent.depth;
        parent.depth++;

        for (Pattern pattern : patternCase.getPatterns()) {
            pattern.accept(this, out);
        }

        out.writeOpcode(END).writeShort(id);

        parent.depth--;

        return null;
    }

    @Override
    public Void visit(WildcardPattern pattern, KDataOutput out) {
        out.writeOpcode(WILDCARD);

        return null;
    }

    @Override
    public Void visit(VariablePattern pattern, KDataOutput out) {
        out.writeOpcode(VARIABLE).writeShort(parent.stringConst(pattern.getName()));

        return null;
    }

    @Override
    public Void visit(TuplePattern pattern, KDataOutput out) {
        out.writeOpcode(TUPLE).writeShort(parent.stringConst(pattern.getName()));

        int id = parent.depth;
        parent.depth++;

        pattern.getPattern().accept(this, out);
        out.writeOpcode(END).writeShort(id);

        parent.depth--;

        return null;
    }

    @Override
    public Void visit(RestPattern pattern, KDataOutput out) {
        out.writeOpcode(REST).writeShort(parent.stringConst(pattern.getName()));

        return null;
    }

    @Override
    public Void visit(ValuePattern pattern, KDataOutput out) {
        out.writeOpcode(VALUE);

        int id = parent.depth;
        parent.depth++;

        pattern.getValue().accept(parent, out);
        out.writeOpcode(END).writeShort(id);

        parent.depth--;

        return null;
    }

    @Override
    public Void visit(DefaultPattern pattern, KDataOutput out) {
        out.writeOpcode(DEFAULT);

        int id = parent.depth;
        parent.depth++;

        pattern.getDelegate().accept(this, out);
        out.writeOpcode(END).writeShort(id);

        pattern.getDefault().accept(parent, out);
        out.writeOpcode(END).writeShort(id);

        parent.depth--;

        return null;
    }
}
