package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.bytecode.io.KDataOutput;

import java.util.List;

import static xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes.*;

public class PatternCompiler implements PatternVisitor<Void, KDataOutput> {
    private final ExprCompiler parent;

    public PatternCompiler(ExprCompiler parent) {
        this.parent = parent;
    }

    @Override
    public Void visit(PatternCase patternCase, KDataOutput out) {
        List<Pattern> patterns = patternCase.getPatterns();

        out.writeOpcode(PATTERN_CASE).writeInt(patterns.size());

        for (Pattern pattern : patterns) {
            pattern.accept(this, out);
        }

        out.writeOpcode(END);

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

        pattern.getPattern().accept(this, out);
        out.writeOpcode(END);

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

        pattern.getValue().accept(parent, out);
        out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(DefaultPattern pattern, KDataOutput out) {
        out.writeOpcode(DEFAULT).writeShort(parent.stringConst(pattern.getDelegate().getName()));

        pattern.getDelegate().accept(this, out);

        pattern.getDefault().accept(parent, out);
        out.writeOpcode(END);

        return null;
    }
}
