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
import xyz.avarel.kaiper.bytecode.DataOutputConsumer;
import xyz.avarel.kaiper.lexer.Position;

import java.util.*;
import java.util.stream.Collectors;

import static xyz.avarel.kaiper.bytecode.Opcodes.*;

public class ExprCompiler implements ExprVisitor<DataOutputConsumer, Void> {

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

    private DataOutputConsumer lineNumber(Position position) {
        long lineNumber = position.getLineNumber();

        if (lineNumber == lastLineNumber) return DataOutputConsumer.NO_OP;

        lastLineNumber = lineNumber;

        return out -> {
            LINE_NUMBER.writeInto(out);
            out.writeLong(lineNumber);
        };
    }

    private DataOutputConsumer lineNumberAndVisit(Expr expr) {
        return lineNumber(expr.getPosition()).andThen(expr.accept(this, null));
    }

    private DataOutputConsumer lineNumber(Expr expr) {
        return lineNumber(expr.getPosition());
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
    public DataOutputConsumer visit(Statements statements, Void scope) {
        Iterator<Expr> iterator = statements.getExprs().iterator();
        if (!iterator.hasNext()) return DataOutputConsumer.NO_OP;

        DataOutputConsumer consumer = lineNumberAndVisit(iterator.next());

        if (!iterator.hasNext()) return consumer;

        consumer = consumer.andThen(POP);

        while (iterator.hasNext()) {
            consumer = consumer.andThen(lineNumberAndVisit(iterator.next()));

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
        DataOutputConsumer patterns = new PatternCompiler().visit(expr.getPatternCase(), this), inner = lineNumberAndVisit(expr);
        regionId--;


        return lineNumber(expr.getPosition()).andThen(out -> {
            NEW_FUNCTION.writeInto(out);
            out.writeShort(name);

            patterns.writeInto(out);

            END.writeInto(out);
            out.writeShort(id);

            inner.writeInto(out);

            END.writeInto(out);
            out.writeShort(id);
        });
    }

    @Override
    public DataOutputConsumer visit(Identifier expr, Void scope) {
        int name = stringConst(expr.getName());

        if (expr.getParent() == null) {
            return lineNumber(expr.getPosition()).andThen(out -> {
                IDENTIFIER.writeInto(out);
                out.writeBoolean(false);
                out.writeShort(name);
            });
        }

        DataOutputConsumer parent = lineNumber(expr).andThen(expr.getParent().accept(this, null));

        return lineNumber(expr.getPosition()).andThen(out -> {
            parent.writeInto(out);
            IDENTIFIER.writeInto(out);
            out.writeBoolean(true);
            out.writeShort(name);
        });
    }

    @Override
    public DataOutputConsumer visit(Invocation expr, Void scope) {
        DataOutputConsumer left = lineNumberAndVisit(expr.getLeft()),
                args = lineNumberAndVisit(expr.getArgument()),
                line = lineNumber(expr);


        return out -> {
            left.writeInto(out);
            args.writeInto(out);
            line.writeInto(out);
            INVOKE.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(BinaryOperation expr, Void scope) {
        DataOutputConsumer left = lineNumberAndVisit(expr.getLeft()),
                right = lineNumberAndVisit(expr.getRight()),
                line = lineNumber(expr);
        int operator = expr.getOperator().ordinal();

        return out -> {
            left.writeInto(out);
            right.writeInto(out);
            line.writeInto(out);
            BINARY_OPERATION.writeInto(out);
            out.writeByte(operator);
        };
    }

    @Override
    public DataOutputConsumer visit(UnaryOperation expr, Void scope) {
        DataOutputConsumer target = lineNumberAndVisit(expr.getTarget()),
                line = lineNumber(expr);
        int operator = expr.getOperator().ordinal();

        return out -> {
            target.writeInto(out);
            line.writeInto(out);
            UNARY_OPERATION.writeInto(out);
            out.writeByte(operator);
        };
    }

    @Override
    public DataOutputConsumer visit(RangeNode expr, Void scope) {
        DataOutputConsumer left = lineNumberAndVisit(expr.getLeft()),
                right = lineNumberAndVisit(expr.getRight()),
                line = lineNumber(expr);

        return out -> {
            left.writeInto(out);
            right.writeInto(out);
            line.writeInto(out);
            NEW_RANGE.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(ArrayNode expr, Void scope) {
        List<Single> items = expr.getItems();

        if (items.isEmpty()) return lineNumber(expr).andThen(out -> {
            NEW_ARRAY.writeInto(out);
            out.writeInt(0);
        });

        DataOutputConsumer consumer = NO_OP;
        int count = 0;
        for (Single item : items) {
            consumer = consumer.andThen(lineNumberAndVisit(item));
        }

        return consumer.andThen(lineNumber(expr).andThen(out -> {
            NEW_ARRAY.writeInto(out);
            out.writeInt(count);
        }));
    }

    @Override
    public DataOutputConsumer visit(SliceOperation expr, Void scope) {
        DataOutputConsumer left = lineNumberAndVisit(expr.getLeft()),
                start = lineNumberAndVisit(expr.getStart()),
                end = lineNumberAndVisit(expr.getEnd()),
                step = lineNumberAndVisit(expr.getStep()),
                line = lineNumber(expr);

        return out -> {
            left.writeInto(out);
            start.writeInto(out);
            end.writeInto(out);
            step.writeInto(out);
            line.writeInto(out);
            SLICE_OPERATION.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(AssignmentExpr expr, Void scope) {
        int name = stringConst(expr.getName());

        DataOutputConsumer data = lineNumberAndVisit(expr.getExpr());

        if (expr.getParent() != null) {

            DataOutputConsumer parent = lineNumberAndVisit(expr.getParent());
            DataOutputConsumer line = lineNumber(expr);

            return out -> {
                parent.writeInto(out);
                data.writeInto(out);
                line.writeInto(out);
                ASSIGN.writeInto(out);
                out.writeBoolean(true);
                out.writeShort(name);
            };
        }

        DataOutputConsumer line = lineNumber(expr);

        return out -> {
            data.writeInto(out);
            line.writeInto(out);
            ASSIGN.writeInto(out);
            out.writeBoolean(false);
            out.writeShort(name);
        };
    }

    @Override
    public DataOutputConsumer visit(GetOperation expr, Void scope) {
        DataOutputConsumer left = lineNumberAndVisit(expr.getLeft()),
                key = lineNumberAndVisit(expr.getKey()),
                line = lineNumber(expr);

        return out -> {
            left.writeInto(out);
            key.writeInto(out);
            line.writeInto(out);
            ARRAY_GET.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(SetOperation expr, Void scope) {
        DataOutputConsumer left = lineNumberAndVisit(expr.getLeft()),
                key = lineNumberAndVisit(expr.getKey()),
                data = lineNumberAndVisit(expr.getExpr()),
                line = lineNumber(expr);

        return out -> {
            left.writeInto(out);
            key.writeInto(out);
            data.writeInto(out);
            line.writeInto(out);
            ARRAY_SET.writeInto(out);
        };
    }

    @Override
    public DataOutputConsumer visit(ReturnExpr expr, Void scope) {
        DataOutputConsumer data = lineNumberAndVisit(expr.getExpr()), line = lineNumber(expr);

        return out -> {
            data.writeInto(out);
            line.writeInto(out);
            RETURN.writeInto(out);
        };
    }

    public DataOutputConsumer visit(ConditionalExpr expr, Void ignored) {
        boolean hasElseBranch = expr.getElseBranch() != null;

        int id = regionId;
        regionId++;
        DataOutputConsumer line = lineNumber(expr);
        DataOutputConsumer condition = lineNumberAndVisit(expr.getCondition());
        DataOutputConsumer ifBranch = lineNumberAndVisit(expr.getIfBranch());
        DataOutputConsumer elseBranch = hasElseBranch ? lineNumberAndVisit(expr.getElseBranch()) : null;
        regionId--;

        if (!hasElseBranch) {
            return out -> {
                line.writeInto(out);
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
            line.writeInto(out);
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
        DataOutputConsumer line = lineNumber(expr);
        DataOutputConsumer iterable = expr.getIterable().accept(this, null);
        DataOutputConsumer action = expr.getAction().accept(this, null);
        regionId--;

        return out -> {
            line.writeInto(out);
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
                ARRAY_SET.writeInto(out);
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
    public DataOutputConsumer visit(BindAssignmentExpr expr, Void scope) {
        return null;
    }

    @Override
    public DataOutputConsumer visit(BindDeclarationExpr expr, Void scope) {
        return null;
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
        int id = regionId;
        regionId++;
        DataOutputConsumer condition = expr.getCondition().accept(this, null);
        DataOutputConsumer action = expr.getAction().accept(this, null);
        regionId--;

        return out -> {
            WHILE.writeInto(out);
            condition.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
            action.writeInto(out);
            END.writeInto(out);
            out.writeShort(id);
        };
    }

    @Override
    public DataOutputConsumer visit(TupleExpr expr, Void scope) {
        return null;
    }
}
