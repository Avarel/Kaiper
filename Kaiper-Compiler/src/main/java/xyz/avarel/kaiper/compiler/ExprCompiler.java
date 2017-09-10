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

import static xyz.avarel.kaiper.bytecode.opcodes.Opcodes.*;

public class ExprCompiler implements ExprVisitor<Void, ByteOutput> {

    private final List<String> stringPool = new LinkedList<>();

    int regionId = 0;
    private long lastLineNumber;

    @Override
    public Void visit(Statements statements, ByteOutput out) {
        Iterator<Expr> iterator = statements.getExprs().iterator();
        if (!iterator.hasNext()) return null;

        while (iterator.hasNext()) {
            visit(out, iterator.next());
            if (iterator.hasNext()) out.writeOpcode(POP);
        }

        return null;
    }

    @Override
    public Void visit(FunctionNode expr, ByteOutput out) {
        String tmp = expr.getName();
        int name = stringConst(tmp == null ? "" : tmp);

        lineNumber(expr, out);

        out.writeOpcode(NEW_FUNCTION).writeShort(name);

        int id = regionId;
        regionId++;

        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        out.writeOpcode(END).writeShort(id);

        visit(out, expr.getExpr());
        out.writeOpcode(END).writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(Identifier expr, ByteOutput out) {
        int name = stringConst(expr.getName());

        lineNumber(expr, out);

        boolean parented = expr.getParent() != null;

        if (parented) {
            visit(out, expr.getParent());
        }

        out.writeOpcode(IDENTIFIER).writeBoolean(parented).writeShort(name);

        return null;
    }

    @Override
    public Void visit(Invocation expr, ByteOutput out) {
        visit(out, expr.getLeft(), expr.getArgument());

        lineNumber(expr, out);

        out.writeOpcode(INVOKE);

        return null;
    }

    @Override
    public Void visit(BinaryOperation expr, ByteOutput out) {
        visit(out, expr.getLeft(), expr.getRight());

        lineNumber(expr, out);

        out.writeOpcode(BINARY_OPERATION).writeByte(expr.getOperator().ordinal());

        return null;
    }

    @Override
    public Void visit(UnaryOperation expr, ByteOutput out) {
        visit(out, expr.getTarget());

        lineNumber(expr, out);

        out.writeOpcode(UNARY_OPERATION).writeByte(expr.getOperator().ordinal());

        return null;
    }

    @Override
    public Void visit(RangeNode expr, ByteOutput out) {
        visit(out, expr.getLeft(), expr.getRight());

        lineNumber(expr, out);

        out.writeOpcode(NEW_RANGE);

        return null;
    }

    @Override
    public Void visit(ArrayNode expr, ByteOutput out) {
        List<Single> items = expr.getItems();

        if (items.isEmpty()) {
            out.writeOpcode(NEW_ARRAY).writeInt(0);

            return null;
        }

        visit(out, expr.getItems());

        lineNumber(expr, out);

        out.writeOpcode(NEW_ARRAY).writeInt(items.size());

        return null;
    }

    @Override
    public Void visit(SliceOperation expr, ByteOutput out) {
        visit(out, expr.getLeft(), expr.getStart(), expr.getEnd(), expr.getStep());

        lineNumber(expr, out);

        out.writeOpcode(SLICE_OPERATION);

        return null;
    }

    @Override
    public Void visit(AssignmentExpr expr, ByteOutput out) {
        int name = stringConst(expr.getName());
        boolean parented = expr.getParent() != null;

        if (parented) visit(out, expr.getParent());
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(ASSIGN).writeBoolean(parented).writeShort(name);

        return null;
    }

    @Override
    public Void visit(GetOperation expr, ByteOutput out) {
        visit(out, expr.getLeft(), expr.getKey());

        lineNumber(expr, out);

        out.writeOpcode(ARRAY_GET);

        return null;
    }

    @Override
    public Void visit(SetOperation expr, ByteOutput out) {
        visit(out, expr.getLeft(), expr.getKey(), expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(ARRAY_SET);

        return null;
    }

    @Override
    public Void visit(ReturnExpr expr, ByteOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);
        out.writeOpcode(RETURN);

        return null;
    }

    public Void visit(ConditionalExpr expr, ByteOutput out) {
        lineNumber(expr, out);

        boolean hasElseBranch = expr.getElseBranch() != null;

        out.writeOpcode(CONDITIONAL).writeBoolean(hasElseBranch);

        int id = regionId;
        regionId++;

        visit(out, expr.getCondition());
        out.writeOpcode(END).writeShort(id);

        visit(out, expr.getIfBranch());
        out.writeOpcode(END).writeShort(id);

        if (hasElseBranch) {
            visit(out, expr.getElseBranch());
            out.writeOpcode(END).writeShort(id);
        }

        regionId--;

        return null;
    }

    public Void visit(ForEachExpr expr, ByteOutput out) {
        int variant = stringConst(expr.getVariant());

        lineNumber(expr, out);

        out.writeOpcode(FOR_EACH).writeShort(variant);

        int id = regionId;
        regionId++;

        visit(out, expr.getIterable());
        out.writeOpcode(END).writeShort(id);

        visit(out, expr.getAction());
        out.writeOpcode(END).writeShort(id);

        regionId--;


        return null;
    }

    @Override
    public Void visit(DictionaryNode expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(NEW_DICTIONARY);

        Map<Single, Single> map = expr.getMap();

        if (map.isEmpty()) return null;

        for (Map.Entry<Single, Single> entry : map.entrySet()) {
            out.writeOpcode(DUP);

            visit(out, entry.getKey(), entry.getValue());

            out.writeOpcode(ARRAY_SET);
        }

        return null;
    }

    @Override
    public Void visit(NullNode expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(U_CONST);

        return null;
    }

    @Override
    public Void visit(IntNode expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(I_CONST).writeInt(expr.getValue());

        return null;
    }

    @Override
    public Void visit(DecimalNode expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(D_CONST).writeDouble(expr.getValue());

        return null;
    }

    @Override
    public Void visit(BooleanNode expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(expr == BooleanNode.TRUE ? B_CONST_TRUE : B_CONST_FALSE);

        return null;
    }

    @Override
    public Void visit(StringNode expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(S_CONST).writeShort(stringConst(expr.getValue()));

        return null;
    }

    @Override
    public Void visit(DeclarationExpr expr, ByteOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        out.writeOpcode(DECLARE).writeShort(name);

        return null;
    }

    @Override
    public Void visit(ModuleNode expr, ByteOutput out) {
        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        out.writeOpcode(NEW_MODULE).writeShort(name);

        int id = regionId;
        regionId++;

        visit(out, expr.getExpr());
        out.writeOpcode(END).writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(TypeNode expr, ByteOutput out) {
        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        out.writeOpcode(NEW_TYPE).writeShort(name);

        int id = regionId;
        regionId++;


        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        out.writeOpcode(END).writeShort(id);

        visit(out, expr.getExpr());
        out.writeOpcode(END).writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(WhileExpr expr, ByteOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(WHILE);

        int id = regionId;
        regionId++;

        visit(out, expr.getCondition());
        out.writeOpcode(END).writeShort(id);

        visit(out, expr.getAction());
        out.writeOpcode(END).writeShort(id);

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

        out.writeOpcode(NEW_TUPLE).writeInt(map.size());

        int id = regionId;
        regionId++;

        for (Map.Entry<String, Single> entry : map.entrySet()) {
            out.writeShort(stringConst(entry.getKey()));

            visit(out, entry.getValue());
            out.writeOpcode(END).writeShort(id);
        }

        regionId--;

        return null;
    }

    @Override
    public Void visit(BindDeclarationExpr expr, ByteOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(BIND_DECLARE);

        int id = regionId;
        regionId++;

        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        out.writeOpcode(END).writeShort(id);

        regionId--;

        return null;
    }

    @Override
    public Void visit(BindAssignmentExpr expr, ByteOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(BIND_ASSIGN);

        int id = regionId;
        regionId++;

        new PatternCompiler(this).visit(expr.getPatternCase(), out);
        out.writeOpcode(END).writeShort(id);

        regionId--;

        return null;
    }

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

        out.writeOpcode(LINE_NUMBER).writeLong(lineNumber);
    }

    private void lineNumber(Expr expr, ByteOutput out) {
        lineNumber(expr.getPosition(), out);
    }

    private void visit(ByteOutput out, Expr... exprs) {
        for (Expr expr : exprs) visit(out, expr);
    }

    private void visit(ByteOutput out, Iterable<? extends Expr> exprs) {
        for (Expr expr : exprs) visit(out, expr);
    }

    private void visit(ByteOutput out, Expr expr) {
        lineNumber(expr.getPosition(), out);
        expr.accept(this, out);
    }

    public void writeStringPool(ByteOutput out) {
        out.writeShort(stringPool.size());
        for (String s : stringPool) out.writeString(s);
    }
}
