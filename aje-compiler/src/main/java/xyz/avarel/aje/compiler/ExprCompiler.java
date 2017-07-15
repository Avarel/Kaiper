package xyz.avarel.aje.compiler;

import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.collections.*;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.flow.ForEachExpr;
import xyz.avarel.aje.ast.flow.ReturnExpr;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.oop.ClassNode;
import xyz.avarel.aje.ast.oop.ConstructorNode;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.ast.value.*;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.DeclarationExpr;
import xyz.avarel.aje.ast.variables.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static xyz.avarel.aje.compiler.Opcodes.*;

public class ExprCompiler implements ExprVisitor<DataOutputConsumer, Void> {
    private static final DataOutputConsumer NO_OP_CONSUMER = out -> {
    };

    private final List<String> stringPool = new LinkedList<>();

    private int stringConst(String s) {
        int i = stringPool.indexOf(s);
        if (i == -1) {
            stringPool.add(s);
            return stringPool.indexOf(s);
        }
        return i;
    }

    public DataOutputConsumer stringPool() {
        ArrayList<String> strings = new ArrayList<>(stringPool);

        return out -> {
            out.writeInt(strings.size());
            for (String s : strings) {
                out.writeUTF(s);
            }
        };
    }

    private DataOutputConsumer zip(Iterator<DataOutputConsumer> iterator) {
        if (!iterator.hasNext()) return NO_OP_CONSUMER;

        DataOutputConsumer consumer = iterator.next();
        if (!iterator.hasNext()) return consumer;

        while (iterator.hasNext()) consumer = consumer.andThen(iterator.next());
        return consumer;
    }

    @Override
    public DataOutputConsumer visit(Statements statements, Void scope) {
        return zip(
                statements.getExprs().stream()
                        .map(expr -> expr.accept(this, null))
                        .iterator()
        );
    }

    @Override
    public DataOutputConsumer visit(FunctionNode expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(Identifier expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(Invocation expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(BinaryOperation expr, Void scope) {
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer right = expr.getRight().accept(this, null);
        int operator = expr.getOperator().ordinal();

        return out -> {
            left.writeInto(out);
            right.writeInto(out);
            BINARY_OPERATION.writeInto(out);
            out.writeByte(operator);
        };
    }

    @Override
    public DataOutputConsumer visit(UnaryOperation expr, Void scope) {
        DataOutputConsumer target = expr.getTarget().accept(this, null);
        int operator = expr.getOperator().ordinal();
        return out -> {
            target.writeInto(out);
            UNARY_OPERATION.writeInto(out);
            out.writeByte(operator);
        };
    }

    @Override
    public DataOutputConsumer visit(RangeNode expr, Void scope) {
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer right = expr.getRight().accept(this, null);

        return out -> {
            left.writeInto(out);
            right.writeInto(out);
            NEW_RANGE.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(ArrayNode expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(SliceOperation expr, Void scope) {
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer start = expr.getStart().accept(this, null);
        DataOutputConsumer end = expr.getEnd().accept(this, null);
        DataOutputConsumer step = expr.getStep().accept(this, null);

        return out -> {
            left.writeInto(out);
            start.writeInto(out);
            end.writeInto(out);
            step.writeInto(out);
            SLICE_OPERATION.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(AssignmentExpr expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(GetOperation expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(SetOperation expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(ReturnExpr expr, Void scope) {
        DataOutputConsumer consumer = expr.getExpr().accept(this, null);

        return out -> {
            consumer.writeInto(out);
            RETURN.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(ConditionalExpr expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(ForEachExpr expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(DictionaryNode expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(UndefinedNode undefinedNode, Void scope) {
        return U_CONST;
    }

    @Override
    public DataOutputConsumer visit(IntNode intNode, Void scope) {
        int constValue = intNode.getValue();

        return out -> {
            I_CONST.writeInto(out);
            out.writeInt(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(DecimalNode decimalNode, Void scope) {
        double constValue = decimalNode.getValue();

        return out -> {
            D_CONST.writeInto(out);
            out.writeDouble(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(BooleanNode booleanNode, Void scope) {
        return booleanNode == BooleanNode.TRUE ? B_CONST_TRUE : B_CONST_FALSE;
    }

    @Override
    public DataOutputConsumer visit(StringNode stringNode, Void scope) {
        int constValue = stringConst(stringNode.getValue());

        return out -> {
            S_CONST.writeInto(out);
            out.writeInt(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(DeclarationExpr declarationExpr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(ClassNode classNode, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(ConstructorNode constructorNode, Void scope) {
        return null;
    }
}
