package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.ModuleNode;
import xyz.avarel.kaiper.ast.expr.TypeNode;
import xyz.avarel.kaiper.ast.expr.collections.ArrayNode;
import xyz.avarel.kaiper.ast.expr.collections.DictionaryNode;
import xyz.avarel.kaiper.ast.expr.collections.GetOperation;
import xyz.avarel.kaiper.ast.expr.collections.SetOperation;
import xyz.avarel.kaiper.ast.expr.flow.*;
import xyz.avarel.kaiper.ast.expr.functions.FunctionNode;
import xyz.avarel.kaiper.ast.expr.invocation.Invocation;
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.expr.operations.SliceOperation;
import xyz.avarel.kaiper.ast.expr.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.expr.tuples.FreeFormStruct;
import xyz.avarel.kaiper.ast.expr.value.*;
import xyz.avarel.kaiper.ast.expr.variables.*;
import xyz.avarel.kaiper.bytecode.io.KDataOutput;
import xyz.avarel.kaiper.lexer.Position;
import xyz.avarel.kaiper.operations.BinaryOperatorType;

import java.util.*;

import static xyz.avarel.kaiper.bytecode.opcodes.KOpcodes.*;
import static xyz.avarel.kaiper.operations.BinaryOperatorType.AND;
import static xyz.avarel.kaiper.operations.BinaryOperatorType.OR;

public class ExprCompiler implements ExprVisitor<Void, KDataOutput> {

    private final List<String> stringPool = new LinkedList<>();
    private final PatternCompiler patternCompiler = new PatternCompiler(this);
    private long lastLineNumber;

    @Override
    public Void visit(Statements statements, KDataOutput out) {
        Iterator<Expr> iterator = statements.getExprs().iterator();
        if (!iterator.hasNext()) return null;

        while (iterator.hasNext()) {
            visit(out, iterator.next());
            if (iterator.hasNext()) out.writeOpcode(POP);
        }

        return null;
    }

    @Override
    public Void visit(FunctionNode expr, KDataOutput out) {
        String tmp = expr.getName();
        int name = stringConst(tmp == null ? "" : tmp);

        lineNumber(expr, out);

        out.writeOpcode(NEW_FUNCTION).writeShort(name);

        patternCompiler.compile(expr.getPatternCase(), out);
        //out.writeOpcode(END);

        visit(out, expr.getExpr());
        out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(Identifier expr, KDataOutput out) {
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
    public Void visit(Invocation expr, KDataOutput out) {
        visit(out, expr.getLeft(), expr.getArgument());

        lineNumber(expr, out);

        out.writeOpcode(INVOKE);

        return null;
    }

    @Override
    public Void visit(BinaryOperation expr, KDataOutput out) {
        BinaryOperatorType op = expr.getOperator();
        if (op == AND || op == OR) {
            visit(out, expr.getLeft());

            lineNumber(expr, out);

            out.writeOpcode(BINARY_OPERATION).writeByte(op.ordinal());

            visit(out, expr.getRight());
            out.writeOpcode(END);
        }

        visit(out, expr.getLeft(), expr.getRight());

        lineNumber(expr, out);

        out.writeOpcode(BINARY_OPERATION).writeByte(op.ordinal());

        return null;
    }

    @Override
    public Void visit(UnaryOperation expr, KDataOutput out) {
        visit(out, expr.getTarget());

        lineNumber(expr, out);

        out.writeOpcode(UNARY_OPERATION).writeByte(expr.getOperator().ordinal());

        return null;
    }

    public Void visit(RangeNode expr, KDataOutput out) {
        visit(out, expr.getLeft(), expr.getRight());

        lineNumber(expr, out);

        out.writeOpcode(NEW_RANGE);

        return null;
    }

    @Override
    public Void visit(ArrayNode expr, KDataOutput out) {
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
    public Void visit(SliceOperation expr, KDataOutput out) {
        visit(out, expr.getLeft(), expr.getStart(), expr.getEnd(), expr.getStep());

        lineNumber(expr, out);

        out.writeOpcode(SLICE_OPERATION);

        return null;
    }

    @Override
    public Void visit(AssignmentExpr expr, KDataOutput out) {
        int name = stringConst(expr.getName());
        boolean parented = expr.getParent() != null;

        if (parented) visit(out, expr.getParent());
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(ASSIGN).writeBoolean(parented).writeShort(name);

        return null;
    }

    @Override
    public Void visit(GetOperation expr, KDataOutput out) {
        visit(out, expr.getLeft(), expr.getKey());

        lineNumber(expr, out);

        out.writeOpcode(ARRAY_GET);

        return null;
    }

    @Override
    public Void visit(SetOperation expr, KDataOutput out) {
        visit(out, expr.getLeft(), expr.getKey(), expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(ARRAY_SET);

        return null;
    }

    @Override
    public Void visit(ReturnExpr expr, KDataOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);
        out.writeOpcode(RETURN);

        return null;
    }

    public Void visit(ConditionalExpr expr, KDataOutput out) {
        lineNumber(expr, out);

        boolean hasElseBranch = expr.getElseBranch() != null;

        out.writeOpcode(CONDITIONAL).writeBoolean(hasElseBranch);

        visit(out, expr.getCondition());
        out.writeOpcode(END);

        visit(out, expr.getIfBranch());
        out.writeOpcode(END);

        if (hasElseBranch) {
            visit(out, expr.getElseBranch());
            out.writeOpcode(END);
        }

        return null;
    }

    public Void visit(ForEachExpr expr, KDataOutput out) {
        int variant = stringConst(expr.getVariant());

        lineNumber(expr, out);

        out.writeOpcode(FOR_EACH).writeShort(variant);

        visit(out, expr.getIterable());
        out.writeOpcode(END);

        visit(out, expr.getAction());
        out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(DictionaryNode expr, KDataOutput out) {
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
    public Void visit(NullNode expr, KDataOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(U_CONST);

        return null;
    }

    @Override
    public Void visit(IntNode expr, KDataOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(I_CONST).writeInt(expr.getValue());

        return null;
    }

    @Override
    public Void visit(DecimalNode expr, KDataOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(D_CONST).writeDouble(expr.getValue());

        return null;
    }

    @Override
    public Void visit(BooleanNode expr, KDataOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(expr == BooleanNode.TRUE ? B_CONST_TRUE : B_CONST_FALSE);

        return null;
    }

    @Override
    public Void visit(StringNode expr, KDataOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(S_CONST).writeShort(stringConst(expr.getValue()));

        return null;
    }

    @Override
    public Void visit(DeclarationExpr expr, KDataOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        out.writeOpcode(DECLARE).writeShort(name);

        return null;
    }

    @Override
    public Void visit(ModuleNode expr, KDataOutput out) {
        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        out.writeOpcode(NEW_MODULE).writeShort(name);

        visit(out, expr.getExpr());
        out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(TypeNode expr, KDataOutput out) {
        lineNumber(expr, out);

        int name = stringConst(expr.getName());

        out.writeOpcode(NEW_TYPE).writeShort(name);

        patternCompiler.compile(expr.getPatternCase(), out);
        //out.writeOpcode(END);

        visit(out, expr.getExpr());
        out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(WhileExpr expr, KDataOutput out) {
        lineNumber(expr, out);

        out.writeOpcode(WHILE);

        visit(out, expr.getCondition());
        out.writeOpcode(END);

        visit(out, expr.getAction());
        out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(FreeFormStruct expr, KDataOutput out) {
        Map<String, Single> map = new LinkedHashMap<>(expr.getElements());
        lineNumber(expr, out);

        out.writeOpcode(NEW_TUPLE).writeInt(map.size());

        for (Map.Entry<String, Single> entry : map.entrySet()) {
            out.writeShort(stringConst(entry.getKey()));

            visit(out, entry.getValue());
            out.writeOpcode(END);
        }

        return null;
    }

    @Override
    public Void visit(BindDeclarationExpr expr, KDataOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(BIND_DECLARE);

        patternCompiler.compile(expr.getPatternCase(), out);
        //out.writeOpcode(END);

        return null;
    }

    @Override
    public Void visit(BindAssignmentExpr expr, KDataOutput out) {
        visit(out, expr.getExpr());

        lineNumber(expr, out);

        out.writeOpcode(BIND_ASSIGN);

        patternCompiler.compile(expr.getPatternCase(), out);
        //out.writeOpcode(END);

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

    private void lineNumber(Position position, KDataOutput out) {
        long lineNumber = position.getLineNumber();

        if (lineNumber == lastLineNumber) return;

        lastLineNumber = lineNumber;

        out.writeOpcode(LINE_NUMBER).writeLong(lineNumber);
    }

    private void lineNumber(Expr expr, KDataOutput out) {
        lineNumber(expr.getPosition(), out);
    }

    private void visit(KDataOutput out, Expr... exprs) {
        for (Expr expr : exprs) visit(out, expr);
    }

    private void visit(KDataOutput out, Iterable<? extends Expr> exprs) {
        for (Expr expr : exprs) visit(out, expr);
    }

    private void visit(KDataOutput out, Expr expr) {
        lineNumber(expr.getPosition(), out);
        expr.accept(this, out);
    }

    public void writeStringPool(KDataOutput out) {
        out.writeShort(stringPool.size());
        for (String s : stringPool) out.writeString(s);
    }
}
