package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.bytecode.DataOutputConsumer;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;

import java.util.Iterator;

import static xyz.avarel.kaiper.bytecode.pattern.PatternOpcodes.*;

public class PatternCompiler implements PatternVisitor<Void, ByteOutput> {
    private final ExprCompiler parent;
    private int regionId = 0;

    public PatternCompiler(ExprCompiler parent) {

        this.parent = parent;
    }

    @Override
    public Void visit(PatternCase patternCase, ByteOutput out) {
        Iterator<DataOutputConsumer> iterator = patternCase.getPatterns().stream().map(pattern -> pattern.accept(this, out)).iterator();

        DataOutputConsumer result = PATTERN_CASE;

        int id = parent.regionId;
        parent.regionId++;

        while (iterator.hasNext()) {
            result = result.andThen(iterator.next());
        }

        parent.regionId--;

        return result.andThen(out -> {
            END.writeInto(out);
            out.writeShort(id);
        });
    }

    @Override
    public Void visit(WildcardPattern pattern, ByteOutput out) {
        return WILDCARD;
    }

    @Override
    public Void visit(VariablePattern pattern, ByteOutput out) {
        int name = parent.stringConst(pattern.getName());

        return out -> {
            VARIABLE.writeInto(out);
            out.writeInt(name);
        };
    }

    @Override
    public Void visit(TuplePattern pattern, ByteOutput out) {
        int name = parent.stringConst(pattern.getName());

        int id = parent.regionId;
        parent.regionId++;

        DataOutputConsumer data = pattern.getPattern().accept(this, out);

        parent.regionId--;

        return out -> {
            TUPLE.writeInto(out);
            out.writeShort(name);
            data.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
        };
    }

    @Override
    public Void visit(RestPattern pattern, ByteOutput out) {
        int name = parent.stringConst(pattern.getName());

        return out -> {
            REST.writeInto(out);
            out.writeInt(name);
        };
    }

    @Override
    public Void visit(ValuePattern pattern, ByteOutput out) {
        return null;
    }

    @Override
    public Void visit(DefaultPattern pattern, ByteOutput out) {
        return null;
    }
}
