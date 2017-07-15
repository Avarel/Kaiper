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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static xyz.avarel.aje.compiler.Opcodes.*;

public class ExprCompiler implements ExprVisitor<DataOutputConsumer, Void> {
    private static final DataOutputConsumer NO_OP_CONSUMER = out -> {};
    private static final DataOutputConsumer B_CONST_TRUE_CONSUMER = out -> out.writeByte(B_CONST_TRUE.ordinal());
    private static final DataOutputConsumer B_CONST_FALSE_CONSUMER = out -> out.writeByte(B_CONST_FALSE.ordinal());
    private static final DataOutputConsumer U_CONST_CONSUMER = out -> out.writeByte(U_CONST.ordinal());

    private final List<String> stringPool = new LinkedList<>();

    private int stringConst(String s) {
        int i = stringPool.indexOf(s);
        if (i == -1) {
            stringPool.add(s);
            return stringPool.indexOf(s);
        }
        return i;
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
        return null;
    }

    @Override
    public DataOutputConsumer visit(UnaryOperation expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(RangeNode expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(ArrayNode expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(SliceOperation expr, Void scope) {
        return null;
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
        return null;
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
        return U_CONST_CONSUMER;
    }

    @Override
    public DataOutputConsumer visit(IntNode intNode, Void scope) {
        int constValue = intNode.getValue();

        return out -> {
            out.writeByte(I_CONST.ordinal());
            out.writeInt(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(DecimalNode decimalNode, Void scope) {
        double constValue = decimalNode.getValue();

        return out -> {
            out.writeByte(D_CONST.ordinal());
            out.writeDouble(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(BooleanNode booleanNode, Void scope) {
        return booleanNode == BooleanNode.TRUE ? B_CONST_TRUE_CONSUMER : B_CONST_FALSE_CONSUMER;
    }

    @Override
    public DataOutputConsumer visit(StringNode stringNode, Void scope) {
        int constValue = stringConst(stringNode.getValue());

        return out -> {
            out.writeByte(S_CONST.ordinal());
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
