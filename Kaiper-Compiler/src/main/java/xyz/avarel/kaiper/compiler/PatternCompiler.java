package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.bytecode.DataOutputConsumer;
import xyz.avarel.kaiper.pattern.*;

import java.util.Iterator;

import static xyz.avarel.kaiper.bytecode.pattern.PatternOpcodes.*;

public class PatternCompiler implements PatternVisitor<DataOutputConsumer, ExprCompiler> {
    private int regionId = 0;

    @Override
    public DataOutputConsumer visit(PatternCase patternCase, ExprCompiler parent) {
        Iterator<DataOutputConsumer> iterator = patternCase.getPatterns().stream().map(pattern -> pattern.accept(this, parent)).iterator();

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
    public DataOutputConsumer visit(WildcardPattern pattern, ExprCompiler parent) {
        return WILDCARD;
    }

    @Override
    public DataOutputConsumer visit(VariablePattern pattern, ExprCompiler parent) {
        int name = parent.stringConst(pattern.getName());

        return out -> {
            VARIABLE.writeInto(out);
            out.writeInt(name);
        };
    }

    @Override
    public DataOutputConsumer visit(TuplePattern pattern, ExprCompiler parent) {
        int name = parent.stringConst(pattern.getName());

        int id = parent.regionId;
        parent.regionId++;

        DataOutputConsumer data = pattern.getPattern().accept(this, parent);

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
    public DataOutputConsumer visit(RestPattern pattern, ExprCompiler parent) {
        int name = parent.stringConst(pattern.getName());

        return out -> {
            REST.writeInto(out);
            out.writeInt(name);
        };
    }

    @Override
    public DataOutputConsumer visit(ValuePattern<?> pattern, ExprCompiler parent) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(DefaultPattern<?> pattern, ExprCompiler parent) {
        return null;
    }
}
