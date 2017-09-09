package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.*;
import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.*;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.*;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;
import xyz.avarel.kaiper.exceptions.CompilerException;
import xyz.avarel.kaiper.lexer.Position;

import java.util.*;

import static xyz.avarel.kaiper.bytecode.Opcodes.*;

public class ExprCompiler implements ExprVisitor<Void, ByteOutput> {

    private final List<String> stringPool = new LinkedList<>();

    int regionId = 0;
    private long lastLineNumber;

    int stringConst(String s) {
        int i = stringPool.indexOf(s);
        if (i == -1) {
            stringPool.add(s);
            return stringPool.indexOf(s);
        }
        return i;
    }

    private void lineNumber(Position position, ByteOutput out) {
        long lineNumber = position.getLineNumber();

        if (lineNumber == lastLineNumber) return;

        lastLineNumber = lineNumber;

        LINE_NUMBER.writeInto(out);
        out.writeLong(lineNumber);
    }

    private void lineNumber(Expr expr, ByteOutput out) {
        lineNumber(expr.getPosition(), out);
    }

    private void lineNumberAndVisit(Expr expr, ByteOutput out) {
        lineNumber(expr.getPosition(), out);
        expr.accept(this, out);
    }

    public void writeStringPool(ByteOutput out) {
        out.writeShort(stringPool.size());
        for (String s : stringPool) {
            out.writeString(s);
        }
    }

    @Override
    public Void visit(Statements statements, ByteOutput out) {
        Iterator<Expr> iterator = statements.getExprs().iterator();
        if (!iterator.hasNext()) return null;

        while (iterator.hasNext()) {
            lineNumberAndVisit(iterator.next(), out);
            if (iterator.hasNext()) POP.writeInto(out);
        }

        return null;
    }

    @Override
    public Void visit(FunctionNode expr, ByteOutput out) {
        String tmp = expr.getName();
        int name = stringConst(tmp == null ? "" : tmp);

        lineNumber(expr, out);

        NEW_FUNCTION.writeInto(out);
        out.writeShort(name);

        int id = regionId;
        regionId++;

        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        END.writeInto(out);
        out.writeShort(id);

        lineNumberAndVisit(expr.getExpr(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(Identifier expr, ByteOutput out) {
        int name = stringConst(expr.getName());

        lineNumber(expr, out);

        boolean parented = expr.getParent() != null;

        if (parented) {
            lineNumberAndVisit(expr.getParent(), out);
        }

        IDENTIFIER.writeInto(out);

        out.writeBoolean(parented);
        out.writeShort(name);

        return null;
    }

    @Override
    public Void visit(Invocation expr, ByteOutput out) {
        lineNumberAndVisit(expr.getLeft(), out);
        lineNumberAndVisit(expr.getArgument(), out);

        lineNumber(expr, out);

        INVOKE.writeInto(out);

        return null;
    }

    @Override
    public Void visit(BinaryOperation expr, ByteOutput out) {
        lineNumberAndVisit(expr.getLeft(), out);
        lineNumberAndVisit(expr.getRight(), out);

        lineNumber(expr, out);

        BINARY_OPERATION.writeInto(out);
        out.writeByte(expr.getOperator().ordinal());

        return null;
    }

    @Override
    public Void visit(UnaryOperation expr, ByteOutput out) {
        lineNumberAndVisit(expr.getTarget(), out);

        lineNumber(expr, out);

        UNARY_OPERATION.writeInto(out);
        out.writeByte(expr.getOperator().ordinal());

        return null;
    }

    @Override
    public Void visit(RangeNode expr, ByteOutput out) {
        lineNumberAndVisit(expr.getLeft(), out);
        lineNumberAndVisit(expr.getRight(), out);

        lineNumber(expr, out);

        NEW_RANGE.writeInto(out);

        return null;
    }

    @Override
    public Void visit(ArrayNode expr, ByteOutput out) {
        List<Single> items = expr.getItems();

        if (items.isEmpty()) {
            NEW_ARRAY.writeInto(out);
            out.writeInt(0);

            return null;
        }

        for (Single sub : expr.getItems()) {
            lineNumberAndVisit(sub, out);
        }

        lineNumber(expr, out);

        NEW_ARRAY.writeInto(out);
        out.writeInt(items.size());

        return null;
    }

    @Override
    public Void visit(SliceOperation expr, ByteOutput out) {
        lineNumberAndVisit(expr.getLeft(), out);
        lineNumberAndVisit(expr.getStart(), out);
        lineNumberAndVisit(expr.getEnd(), out);
        lineNumberAndVisit(expr.getStep(), out);

        lineNumber(expr, out);

        SLICE_OPERATION.writeInto(out);

        return null;
    }

    @Override
    public Void visit(AssignmentExpr expr, ByteOutput out) {
        int name = stringConst(expr.getName());
        boolean parented = expr.getParent() != null;

        if (parented) lineNumberAndVisit(expr.getParent(), out);
        lineNumberAndVisit(expr.getExpr(), out);

        lineNumber(expr, out);

        ASSIGN.writeInto(out);
        out.writeBoolean(parented);
        out.writeShort(name);

        return null;
    }

    @Override
    public Void visit(GetOperation expr, ByteOutput out) {
        lineNumberAndVisit(expr.getLeft(), out);
        lineNumberAndVisit(expr.getKey(), out);

        lineNumber(expr, out);

        ARRAY_GET.writeInto(out);

        return null;
    }

    @Override
    public Void visit(SetOperation expr, ByteOutput out) {
        lineNumberAndVisit(expr.getLeft(), out);
        lineNumberAndVisit(expr.getKey(), out);
        lineNumberAndVisit(expr.getExpr(), out);

        lineNumber(expr, out);

        ARRAY_SET.writeInto(out);

        return null;
    }

    @Override
    public Void visit(ReturnExpr expr, ByteOutput out) {
        lineNumberAndVisit(expr.getExpr(), out);

        lineNumber(expr, out);
        RETURN.writeInto(out);

        return null;
    }

    public Void visit(ConditionalExpr expr, ByteOutput out) {
        lineNumber(expr, out);

        CONDITIONAL.writeInto(out);
        boolean hasElseBranch = expr.getElseBranch() != null;
        out.writeBoolean(hasElseBranch);

        int id = regionId;
        regionId++;

        lineNumberAndVisit(expr.getCondition(), out);
        END.writeInto(out);
        out.writeShort(id);

        lineNumberAndVisit(expr.getIfBranch(), out);
        END.writeInto(out);
        out.writeShort(id);

        if (hasElseBranch) {
            lineNumberAndVisit(expr.getElseBranch(), out);
            END.writeInto(out);
            out.writeShort(id);
        }

        regionId--;

        return null;
    }

    public Void visit(ForEachExpr expr, ByteOutput out) {
        int variant = stringConst(expr.getVariant());

        lineNumber(expr, out);

        FOR_EACH.writeInto(out);
        out.writeShort(variant);

        int id = regionId;
        regionId++;

        lineNumberAndVisit(expr.getIterable(), out);
        END.writeInto(out);
        out.writeShort(id);

        lineNumberAndVisit(expr.getAction(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;


        return null;
    }

    @Override
    public Void visit(DictionaryNode expr, ByteOutput out) {
        lineNumber(expr, out);

        NEW_DICTIONARY.writeInto(out);

        Map<Single, Single> map = expr.getMap();
        if (map.isEmpty()) return null;

        for (Map.Entry<Single, Single> entry : map.entrySet()) {
            DUP.writeInto(out);

            lineNumberAndVisit(entry.getKey(), out);
            lineNumberAndVisit(entry.getValue(), out);

            ARRAY_SET.writeInto(out);
        }

        return null;
    }

    @Override
    public Void visit(NullNode expr, ByteOutput out) {
        lineNumber(expr, out);

        U_CONST.writeInto(out);

        return null;
    }

    @Override
    public Void visit(IntNode expr, ByteOutput out) {
        lineNumber(expr, out);

        I_CONST.writeInto(out);
        out.writeInt(expr.getValue());

        return null;
    }

    @Override
    public Void visit(DecimalNode expr, ByteOutput out) {
        lineNumber(expr, out);

        D_CONST.writeInto(out);
        out.writeDouble(expr.getValue());

        return null;
    }

    @Override
    public Void visit(BooleanNode expr, ByteOutput out) {
        lineNumber(expr, out);

        (expr == BooleanNode.TRUE ? B_CONST_TRUE : B_CONST_FALSE).writeInto(out);

        return null;
    }

    @Override
    public Void visit(StringNode expr, ByteOutput out) {
        lineNumber(expr, out);

        S_CONST.writeInto(out);
        out.writeShort(stringConst(expr.getValue()));

        return null;
    }

    @Override
    public Void visit(DeclarationExpr expr, ByteOutput out) {
        lineNumberAndVisit(expr.getExpr(), out);

        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        DECLARE.writeInto(out);
        out.writeShort(name);

        return null;
    }

    @Override
    public Void visit(BindAssignmentExpr expr, ByteOutput out) {
        lineNumberAndVisit(expr.getExpr(), out);

        lineNumber(expr, out);

        BIND_ASSIGN.writeInto(out);

        int id = regionId;
        regionId++;

        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(BindDeclarationExpr expr, ByteOutput out) {
        lineNumberAndVisit(expr.getExpr(), out);

        lineNumber(expr, out);

        BIND_DECLARE.writeInto(out);

        int id = regionId;
        regionId++;

        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(ModuleNode expr, ByteOutput out) {
        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        NEW_MODULE.writeInto(out);
        out.writeShort(name);

        int id = regionId;
        regionId++;

        lineNumberAndVisit(expr.getExpr(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(TypeNode expr, ByteOutput out) {
        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        NEW_TYPE.writeInto(out);
        out.writeShort(name);

        int id = regionId;
        regionId++;


        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        END.writeInto(out);
        out.writeShort(id);

        lineNumberAndVisit(expr.getExpr(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(WhileExpr expr, ByteOutput out) {
        lineNumber(expr, out);

        WHILE.writeInto(out);

        int id = regionId;
        regionId++;

        lineNumberAndVisit(expr.getCondition(), out);
        END.writeInto(out);
        out.writeShort(id);

        lineNumberAndVisit(expr.getAction(), out);
        END.writeInto(out);
        out.writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(TupleExpr expr, ByteOutput out) {
        Map<String, Single> map = new LinkedHashMap<>();

        List<Single> unnamedElements = expr.getUnnamedElements();
        for (int i = 0; i < unnamedElements.size(); i++) {
            Single element = unnamedElements.get(i);
            map.put("_" + i, element);
        }

        for (Map.Entry<String, Single> entry : expr.getNamedElements().entrySet()) {
            if (map.containsKey(entry.getKey())) {
                throw new CompilerException("Duplicate field name " + entry.getKey(), entry.getValue().getPosition());
            }

            map.put(entry.getKey(), entry.getValue());
        }

        lineNumber(expr, out);

        NEW_TUPLE.writeInto(out);

        out.writeInt(map.size());

        int id = regionId;
        regionId++;

        for (Map.Entry<String, Single> entry : map.entrySet()) {
            out.writeShort(stringConst(entry.getKey()));

            lineNumberAndVisit(entry.getValue(), out);
            END.writeInto(out);
            out.writeShort(id);
        }

        regionId--;

        return null;
    }
}
