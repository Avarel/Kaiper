/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aje.bytecode.serialization;

import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.collections.*;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.flow.ForEachExpr;
import xyz.avarel.aje.ast.flow.ReturnExpr;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.ast.value.*;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;

import java.util.Iterator;
import java.util.stream.Collectors;

import static xyz.avarel.aje.bytecode.Bytecode.*;

/**
 * Serialize AJE nodes into a {@link DataOutputConsumer} that can write AJE
 * nodes into a {@code byte[]}.
 *
 * @author AdrianTodt
 */
public class ExprSerializer implements ExprVisitor<DataOutputConsumer, Void> {
    private static final DataOutputConsumer NO_OP_CONSUMER = os -> {};

    /**
     * <p>This variable stores the id of the END instruction, used in other instructions.</p>
     * <p>Example: {@link ExprSerializer#visit(ReturnExpr, Void)}</p>
     * <pre>
     * ...
     * int id = endId;
     * endId++;
     * DataOutputConsumer data = expr.getExpr().accept(this);
     * endId--;
     * ...
     * </pre>
     * <p>The code above specifies that the "finish" of the bytecode.</p>
     */
    private int endId = 0;

    public DataOutputConsumer visit(Statements statements, Void ignored) {
        return zip(statements.getExprs().stream().map(expr -> expr.accept(this, null)).collect(Collectors.toList()));
    }

    // func print(str: String, n: Int) { for (x in 0..n) { print(str) } }
    public DataOutputConsumer visit(FunctionNode expr, Void ignored) {
        /*
        FUNCTION name endId (... => parameters) END endId (... => expr) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer childs = zip(expr.getParameterExprs().stream().map(this::visitParameter).collect(Collectors.toList()));
        DataOutputConsumer data = expr.getExpr().accept(this, null);
        endId--;

        String tmp = expr.getName();
        String name = tmp == null ? "" : tmp;

        return output -> {
            output.writeByte(FUNCTION.id());
            output.writeUTF(name);
            output.writeInt(id);
            childs.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            data.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(Identifier expr, Void ignored) {
        /*
        IDENTIFIER parented name [parented ? endId (... => parent) END endId]
         */
        if (expr.getParent() == null) {
            return output -> {
                output.writeByte(IDENTIFIER.id());
                output.writeBoolean(false);
                output.writeUTF(expr.getName());
            };
        }

        int id = endId;
        endId++;
        DataOutputConsumer parent = expr.getParent().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(IDENTIFIER.id());
            output.writeBoolean(true);
            output.writeUTF(expr.getName());
            output.writeInt(id);
            parent.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(Invocation expr, Void ignored) {
        /*
        INVOCATION endId (... => left) END endId (... => arguments) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer arguments = zip(expr.getArguments().stream().map(arg -> arg.accept(this, null)).collect(Collectors.toList()));
        endId--;

        return output -> {
            output.writeByte(INVOCATION.id());
            output.writeInt(id);
            left.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            arguments.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(BinaryOperation expr, Void ignored) {
        /*
        BINARY_OP type endId (... => left) END endId (... => right) END endId
         */

        int id = endId;
        endId++;
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer right = expr.getRight().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(BINARY_OP.id());
            output.writeInt(expr.getOperator().ordinal());
            output.writeInt(id);
            left.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            right.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(UnaryOperation expr, Void ignored) {
        /*
        UNARY_OP type endId (... => target) END endId
         */

        int id = endId;
        endId++;
        DataOutputConsumer target = expr.getTarget().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(BINARY_OP.id());
            output.writeInt(expr.getOperator().ordinal());
            output.writeInt(id);
            target.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(RangeNode expr, Void ignored) {
        /*
        RANGE endId (... => left) END endId (... => right) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer right = expr.getRight().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(RANGE.id());
            output.writeInt(id);
            left.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            right.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(ArrayNode expr, Void ignored) {
        /*
        ARRAY endId (... => childs) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer childs = zip(expr.getItems().stream().map(item -> item.accept(this, null)).collect(Collectors.toList()));
        endId--;

        return output -> {
            output.writeByte(ARRAY.id());
            output.writeInt(id);
            childs.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(SliceOperation expr, Void ignored) {
        /*
        SLICE_OP endId (... => obj) END endId (... => start) END endId (... => end) END endId (... => step) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer obj = expr.getLeft().accept(this, null);
        DataOutputConsumer start = expr.getStart().accept(this, null);
        DataOutputConsumer end = expr.getEnd().accept(this, null);
        DataOutputConsumer step = expr.getStep().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(SLICE_OP.id());
            output.writeInt(id);
            obj.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            start.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            end.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            step.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(AssignmentExpr expr, Void ignored) {
        /*
        ASSIGN declaration name hasParent endId [hasParent ? (... => parent) END endId] (... => expr) END endId
         */
        if (expr.getParent() == null) {
            int id = endId;
            endId++;
            DataOutputConsumer data = expr.getExpr().accept(this, null);
            endId--;

            return output -> {
                output.writeByte(ASSIGN.id());
                output.writeBoolean(expr.isDeclaration());
                output.writeUTF(expr.getName());
                output.writeBoolean(false);
                output.writeInt(id);
                data.writeInto(output);
                output.writeByte(END.id());
                output.writeInt(id);
            };
        } else {
            int id = endId;
            endId++;
            DataOutputConsumer parent = expr.getParent().accept(this, null);
            DataOutputConsumer data = expr.getExpr().accept(this, null);
            endId--;

            return output -> {
                output.writeByte(ASSIGN.id());
                output.writeBoolean(expr.isDeclaration());
                output.writeUTF(expr.getName());
                output.writeBoolean(true);
                output.writeInt(id);
                parent.writeInto(output);
                output.writeByte(END.id());
                output.writeInt(id);
                data.writeInto(output);
                output.writeByte(END.id());
                output.writeInt(id);
            };
        }
    }

    public DataOutputConsumer visit(GetOperation expr, Void ignored) {
        /*
        GET endId (... => left) END endId (... => key) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer key = expr.getKey().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(GET.id());
            output.writeInt(id);
            left.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            key.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(SetOperation expr, Void ignored) {
        /*
        SET endId (... => left) END endId (... => key) END endId (... => value) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer left = expr.getLeft().accept(this, null);
        DataOutputConsumer key = expr.getKey().accept(this, null);
        DataOutputConsumer value = expr.getExpr().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(SET.id());
            output.writeInt(id);
            left.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            key.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            value.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(ReturnExpr expr, Void ignored) {
        /*
        RETURN endId (... => expr) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer data = expr.getExpr().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(RETURN.id());
            output.writeInt(id);
            data.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(ConditionalExpr expr, Void ignored) {
        /*
        CONDITIONAL endId (... => condition) END endId (... => ifBranch) END endId (... => elseBranch) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer condition = expr.getCondition().accept(this, null);
        DataOutputConsumer ifBranch = expr.getIfBranch().accept(this, null);
        DataOutputConsumer elseBranch = expr.getElseBranch().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(CONDITIONAL.id());
            output.writeInt(id);
            condition.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            ifBranch.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            elseBranch.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(ForEachExpr expr, Void ignored) {
        /*
        FOREACH variant endId (... => iterable) END endId (... => action) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer iterable = expr.getIterable().accept(this, null);
        DataOutputConsumer action = expr.getAction().accept(this, null);
        endId--;

        return output -> {
            output.writeByte(FOREACH.id());
            output.writeUTF(expr.getVariant());
            output.writeInt(id);
            iterable.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
            action.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    public DataOutputConsumer visit(DictionaryNode expr, Void ignored) {
        /*
        DICTIONARY endId (... => (key,value)*) END endId
         */
        int id = endId;
        endId++;
        DataOutputConsumer childs = zip(
                expr.getMap().entrySet().stream()
                        .map(entry -> entry.getKey().accept(this, null).andThen(entry.getValue().accept(this, null)))
                        .collect(Collectors.toList())
        );
        endId--;

        return output -> {
            output.writeByte(DICTIONARY.id());
            output.writeInt(id);
            childs.writeInto(output);
            output.writeByte(END.id());
            output.writeInt(id);
        };
    }

    @Override
    public DataOutputConsumer visit(UndefinedNode expr, Void scope) {
        /*
        UNDEFINED
         */
        return output -> output.writeByte(UNDEFINED.id());
    }

    @Override
    public DataOutputConsumer visit(IntNode expr, Void scope) {
        /*
        INT int
         */
        return output -> {
            output.writeByte(INT.id());
            output.writeInt(expr.getValue());
        };
    }

    @Override
    public DataOutputConsumer visit(DecimalNode expr, Void scope) {
        /*
        DECIMAL decimal
         */
        return output -> {
            output.writeByte(DECIMAL.id());
            output.writeDouble(expr.getValue());
        };
    }

    @Override
    public DataOutputConsumer visit(BooleanNode expr, Void scope) {
        /*
        BOOLEAN bool
         */
        return output -> {
            output.writeByte(BOOLEAN.id());
            output.writeBoolean(expr == BooleanNode.TRUE);
        };
    }

    @Override
    public DataOutputConsumer visit(StringNode expr, Void scope) {
        /*
        STRING string
         */
        return output -> {
            output.writeByte(STRING.id());
            output.writeUTF(expr.getValue());
        };
    }

    private DataOutputConsumer visitParameter(ParameterData data) {
        /*
        FUNCTION_PARAM name modifiers endId (... => typeExpr) END endId [modifiers.hasDefaultValue ? (... => defaultValue) END endId]
         */
        if (data.getDefault() == null) {
            int modifiers = data.isRest() ? 0b10 : 0b00;

            int id = endId;
            endId++;
            DataOutputConsumer typeExpr = data.getTypeExpr().accept(this, null);
            endId--;

            return output -> {
                output.writeByte(FUNCTION_PARAM.id());
                output.writeUTF(data.getName());
                output.writeInt(modifiers);
                output.writeInt(id);
                typeExpr.writeInto(output);
                output.writeByte(END.id());
                output.writeInt(id);
            };
        } else {
            int modifiers = data.isRest() ? 0b11 : 0b01;

            int id = endId;
            endId++;
            DataOutputConsumer typeExpr = data.getTypeExpr().accept(this, null);
            DataOutputConsumer defaultValue = data.getDefault().accept(this, null);
            endId--;

            return output -> {
                output.writeByte(FUNCTION_PARAM.id());
                output.writeUTF(data.getName());
                output.writeInt(modifiers);
                output.writeInt(id);
                typeExpr.writeInto(output);
                output.writeByte(END.id());
                output.writeInt(id);
                defaultValue.writeInto(output);
                output.writeByte(END.id());
                output.writeInt(id);
            };
        }
    }

    private DataOutputConsumer zip(Iterable<DataOutputConsumer> consumers) {
        Iterator<DataOutputConsumer> iterator = consumers.iterator();
        if (!iterator.hasNext()) return NO_OP_CONSUMER;

        DataOutputConsumer consumer = iterator.next();
        if (!iterator.hasNext()) return consumer;

        while (iterator.hasNext()) consumer = consumer.andThen(iterator.next());
        return consumer;
    }
}
