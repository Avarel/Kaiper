package xyz.avarel.kaiper.compiler;

import xyz.avarel.kaiper.ast.*;
import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.*;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.functions.ParameterData;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.DeclarationExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.bytecode.DataOutputConsumer;
import xyz.avarel.kaiper.operations.BinaryOperatorType;

import java.util.*;
import java.util.stream.Collectors;

import static xyz.avarel.kaiper.bytecode.Opcodes.*;

public class ExprCompiler implements ExprVisitor<DataOutputConsumer, Void> {
    private static final DataOutputConsumer NO_OP_CONSUMER = out -> {};

    private final List<String> stringPool = new LinkedList<>();
    private int regionId = 0;

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
            out.writeShort(strings.size());
            for (String s : strings) {
                out.writeUTF(s);
            }
        };
    }

    @Override
    public DataOutputConsumer visit(Statements expr, Void scope) {
        Iterator<Expr> iterator = expr.getExprs().iterator();
        if (!iterator.hasNext()) return NO_OP_CONSUMER;

        DataOutputConsumer consumer = iterator.next().accept(this, null);
        if (!iterator.hasNext()) return consumer;

        consumer = consumer.andThen(POP);

        while (iterator.hasNext()) {
            consumer = consumer.andThen(iterator.next().accept(this, null));
            if (iterator.hasNext()) consumer = consumer.andThen(POP);
        }

        return consumer;
    }

    @Override
    public DataOutputConsumer visit(FunctionNode expr, Void scope) {
        String tmp = expr.getName();
        int name = stringConst(tmp == null ? "" : tmp);

        int id = regionId;
        regionId++;
        List<DataOutputConsumer> params = expr.getParameterExprs().stream().map(this::visitParameter).collect(Collectors.toList());
        DataOutputConsumer inner = expr.getExpr().accept(this, null);
        regionId--;


        DataOutputConsumer result = out -> {
            NEW_FUNCTION.writeInto(out);
            out.writeShort(name);
        };

        for (DataOutputConsumer param : params) result = result.andThen(param);

        return result.andThen(out -> {
            END.writeInto(out);
            out.writeShort(id);
            inner.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
        });
    }

    private DataOutputConsumer visitParameter(ParameterData data) {
        boolean hasDefaultValue = data.getDefault() != null;

        int modifiers = (hasDefaultValue ? 1 : 0) | (data.isRest() ? 2 : 0);

        int name = stringConst(data.getName());

        DataOutputConsumer consumer = out -> {
            FUNCTION_DEF_PARAM.writeInto(out);
            out.writeByte(modifiers);
            out.writeShort(name);
        };

        if (hasDefaultValue) {
            int id = regionId;
            regionId++;
            DataOutputConsumer defaultValue = data.getDefault().accept(this, null);
            regionId--;

            consumer = consumer.andThen(out -> {
                defaultValue.writeInto(out);
                END.writeInto(out);
                out.writeShort(id);
            });
        }

        return consumer;
    }

    @Override
    public DataOutputConsumer visit(Identifier expr, Void scope) {
        int name = stringConst(expr.getName());

        if (expr.getParent() == null) {
            return out -> {
                IDENTIFIER.writeInto(out);
                out.writeBoolean(false);
                out.writeShort(name);
            };
        }

        DataOutputConsumer parent = expr.getParent().accept(this, null);

        return out -> {
            parent.writeInto(out);
            IDENTIFIER.writeInto(out);
            out.writeBoolean(true);
            out.writeShort(name);
        };
    }

    @Override
    public DataOutputConsumer visit(Invocation expr, Void scope) {
        DataOutputConsumer tmp = expr.getLeft().accept(this, null), args = NO_OP_CONSUMER;
        for (Single arg : expr.getArgument()) {
            args = args.andThen(arg.accept(this, null));
        }

        DataOutputConsumer data = tmp.andThen(args);
        int pCount = expr.getArgument().size();

        return out -> {
            data.writeInto(out);
            INVOKE.writeInto(out);
            out.writeByte(pCount);
        };
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
        List<Single> items = expr.getItems();
        if (items.isEmpty()) return NEW_ARRAY;

        DataOutputConsumer consumer = NEW_ARRAY;
        for (Single item : items) {
            DataOutputConsumer data = item.accept(this, null);
            consumer = consumer.andThen(out -> {
                DUP.writeInto(out);
                data.writeInto(out);
                BINARY_OPERATION.writeInto(out);
                out.writeByte(BinaryOperatorType.SHL.ordinal());
                POP.writeInto(out);
            });
        }

        return consumer;
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
        int name = stringConst(expr.getName());
        DataOutputConsumer data = expr.getExpr().accept(this, null);

        if (expr.getParent() == null) {
            return out -> {
                data.writeInto(out);
                ASSIGN.writeInto(out);
                out.writeBoolean(false);
                out.writeShort(name);
            };
        }

        DataOutputConsumer parent = expr.getParent().accept(this, null);

        return out -> {
            parent.writeInto(out);
            data.writeInto(out);
            ASSIGN.writeInto(out);
            out.writeBoolean(true);
            out.writeShort(name);
        };
    }

    @Override
    public DataOutputConsumer visit(GetOperation expr, Void scope) {
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer key = expr.getKey().accept(this, null);

        return out -> {
            left.writeInto(out);
            key.writeInto(out);
            GET.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(SetOperation expr, Void scope) {
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer key = expr.getKey().accept(this, null);
        DataOutputConsumer data = expr.getExpr().accept(this, null);

        return out -> {
            left.writeInto(out);
            key.writeInto(out);
            data.writeInto(out);
            SET.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(ReturnExpr expr, Void scope) {
        DataOutputConsumer consumer = expr.getExpr().accept(this, null);

        return out -> {
            consumer.writeInto(out);
            RETURN.writeInto(out);
        };
    }

    public DataOutputConsumer visit(ConditionalExpr expr, Void ignored) {
        boolean hasElseBranch = expr.getElseBranch() != null;

        int id = regionId;
        regionId++;
        DataOutputConsumer condition = expr.getCondition().accept(this, null);
        DataOutputConsumer ifBranch = expr.getIfBranch().accept(this, null);
        DataOutputConsumer elseBranch = hasElseBranch ? expr.getElseBranch().accept(this, null) : null;
        regionId--;

        if (!hasElseBranch) {
            return out -> {
                CONDITIONAL.writeInto(out);
                out.writeBoolean(false);
                condition.writeInto(out);
                END.writeInto(out);
                out.writeShort(id);
                ifBranch.writeInto(out);
                END.writeInto(out);
                out.writeShort(id);
            };
        }

        return out -> {
            CONDITIONAL.writeInto(out);
            out.writeBoolean(true);
            condition.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
            ifBranch.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
            elseBranch.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
        };
    }

    public DataOutputConsumer visit(ForEachExpr expr, Void ignored) {
        int variant = stringConst(expr.getVariant());

        int id = regionId;
        regionId++;
        DataOutputConsumer iterable = expr.getIterable().accept(this, null);
        DataOutputConsumer action = expr.getAction().accept(this, null);
        regionId--;

        return out -> {
            FOR_EACH.writeInto(out);
            out.writeShort(variant);
            iterable.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
            action.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
        };
    }

    @Override
    public DataOutputConsumer visit(DictionaryNode expr, Void scope) {
        Map<Single, Single> map = expr.getMap();
        if (map.isEmpty()) return NEW_DICTIONARY;

        DataOutputConsumer consumer = NEW_DICTIONARY;
        for (Map.Entry<Single, Single> entry : map.entrySet()) {
            DataOutputConsumer key = entry.getKey().accept(this, null);
            DataOutputConsumer value = entry.getValue().accept(this, null);

            consumer = consumer.andThen(out -> {
                DUP.writeInto(out);
                key.writeInto(out);
                value.writeInto(out);
                SET.writeInto(out);
            });
        }

        return consumer;
    }

    @Override
    public DataOutputConsumer visit(NullNode expr, Void scope) {
        return U_CONST;
    }

    @Override
    public DataOutputConsumer visit(IntNode expr, Void scope) {
        int constValue = expr.getValue();

        return out -> {
            I_CONST.writeInto(out);
            out.writeInt(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(DecimalNode expr, Void scope) {
        double constValue = expr.getValue();

        return out -> {
            D_CONST.writeInto(out);
            out.writeDouble(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(BooleanNode expr, Void scope) {
        return expr == BooleanNode.TRUE ? B_CONST_TRUE : B_CONST_FALSE;
    }

    @Override
    public DataOutputConsumer visit(StringNode expr, Void scope) {
        int constValue = stringConst(expr.getValue());

        return out -> {
            S_CONST.writeInto(out);
            out.writeShort(constValue);
        };
    }

    @Override
    public DataOutputConsumer visit(DeclarationExpr expr, Void scope) {
        int name = stringConst(expr.getName());
        DataOutputConsumer consumer = expr.getExpr().accept(this, null);

        return out -> {
            consumer.writeInto(out);
            DECLARE.writeInto(out);
            out.writeShort(name);
        };
    }

    @Override
    public DataOutputConsumer visit(ModuleNode expr, Void scope) {
        int name = stringConst(expr.getName());

        int id = regionId;
        regionId++;
        DataOutputConsumer inner = expr.getExpr().accept(this, null);
        regionId--;


        return out -> {
            NEW_MODULE.writeInto(out);
            out.writeShort(name);
            inner.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
        };
    }

    @Override
    public DataOutputConsumer visit(TypeNode expr, Void scope) {
        int name = stringConst(expr.getName());
        int id = regionId;
        regionId++;
        List<DataOutputConsumer> params = expr.getParameterExprs().stream().map(this::visitParameter).collect(Collectors.toList());
        DataOutputConsumer superType = expr.getSuperType().accept(this, null);
        List<DataOutputConsumer> superParams = expr.getSuperParameters().stream().map(e -> e.accept(this, null)).collect(Collectors.toList());
        DataOutputConsumer inner = expr.getExpr().accept(this, null);
        regionId--;


        DataOutputConsumer result = out -> {
            NEW_TYPE.writeInto(out);
            out.writeShort(name);
        };

        for (DataOutputConsumer param : params) result = result.andThen(param);

        result = result.andThen(out -> {
            END.writeInto(out);
            out.writeShort(id);

            superType.writeInto(out);

            END.writeInto(out);
            out.writeShort(id);
        });

        for (DataOutputConsumer param : superParams) result = result.andThen(param);

        return result.andThen(out -> {
            END.writeInto(out);
            out.writeShort(id);

            inner.writeInto(out);

            END.writeInto(out);
            out.writeShort(id);
        });
    }

    @Override
    public DataOutputConsumer visit(WhileExpr expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(TupleExpr expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(TupleEntry expr, Void scope) {
        return null;
    }
}
