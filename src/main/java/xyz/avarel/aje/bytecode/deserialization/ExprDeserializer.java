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

package xyz.avarel.aje.bytecode.deserialization;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.collections.*;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.flow.ForEachExpr;
import xyz.avarel.aje.ast.flow.ReturnExpr;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.operations.*;
import xyz.avarel.aje.ast.value.*;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.bytecode.AJEBytecode;
import xyz.avarel.aje.bytecode.Bytecode;
import xyz.avarel.aje.exceptions.InvalidBytecodeException;

import java.io.DataInput;
import java.io.IOException;
import java.util.*;

/**
 * Deserialize a {@link DataInput} into AJE nodes.
 *
 * @author AdrianTodt
 */
public class ExprDeserializer {
    private final DataInput input;

    public ExprDeserializer(DataInput input) throws IOException {
        this.input = AJEBytecode.validateInit(input);
    }

    private Expr collectUntil(int validEndId) throws IOException {
        Expr expr = nextExpr(validEndId);
        if (expr == null) throw new InvalidBytecodeException("Empty Expr");

        Expr next;
        while ((next = nextExpr(validEndId)) != null) {
            expr = expr.andThen(next);
        }
        return expr;
    }

    public Expr deserializeAll() throws IOException {
        return collectUntil(-1);
    }

    public Expr nextExpr() throws IOException {
        return nextExpr(-1);
    }

    private Expr nextExpr(int validEndId) throws IOException {
        switch (Bytecode.byID(input.readByte())) {
            case END: {
                /*
                END endId
                 */
                int endId = input.readInt();
                if (endId != validEndId) throw new InvalidBytecodeException("Invalid END");
                return null;
            }
            case RETURN: {
                /*
                RETURN endId (... => expr) END endId
                 */
                int endId = input.readInt();
                return new ReturnExpr(collectUntil(endId));
            }
            case ASSIGN: {
                /*
                ASSIGN declaration name hasParent endId [hasParent ? (... => parent) END endId] (... => expr) END endId
                 */
                boolean declaration = input.readBoolean();
                String name = input.readUTF();
                boolean hasParent = input.readBoolean();
                int endId = input.readInt();
                if (hasParent) {
                    return new AssignmentExpr(collectUntil(endId), name, collectUntil(endId), declaration);
                } else {
                    return new AssignmentExpr(null, name, collectUntil(endId), declaration);
                }

            }
            case CONDITIONAL: {
                /*
                CONDITIONAL endId (... => condition) END endId (... => ifBranch) END endId (... => elseBranch) END endId
                 */
                int endId = input.readInt();
                return new ConditionalExpr(collectUntil(endId), collectUntil(endId), collectUntil(endId));
            }
            case FOREACH: {
                /*
                FOREACH variant endId (... => iterable) END endId (... => action) END endId
                 */
                String variant = input.readUTF();
                int endId = input.readInt();
                return new ForEachExpr(variant, collectUntil(endId), collectUntil(endId));
            }
            case ARRAY: {
                /*
                ARRAY endId (... => childs) END endId
                 */
                int endId = input.readInt();
                return new ArrayNode(toList(collectUntil(endId)));
            }
            case DICTIONARY: {
                /*
                DICTIONARY endId (... => (key,value)*) END endId
                 */
                int endId = input.readInt();
                List<Expr> exprs = toList(collectUntil(endId));

                if ((exprs.size() & 1) == 1) throw new InvalidBytecodeException("Unmatched Instruction Pairs");

                Map<Expr, Expr> map = new HashMap<>();

                Iterator<Expr> iterator = exprs.iterator();
                while (iterator.hasNext()) {
                    map.put(iterator.next(), iterator.next());
                }

                return new DictionaryNode(map);
            }
            case RANGE: {
                /*
                RANGE endId (... => left) END endId (... => right) END endId
                 */
                int endId = input.readInt();
                return new RangeNode(collectUntil(endId), collectUntil(endId));
            }
            case UNDEFINED: {
                /*
                UNDEFINED
                 */
                return UndefinedNode.VALUE;
            }
            case BOOLEAN: {
                /*
                BOOLEAN bool
                 */
                return input.readBoolean() ? BooleanNode.TRUE : BooleanNode.FALSE;
            }
            case INT: {
                /*
                INT int
                 */
                return new IntNode(input.readInt());
            }
            case DECIMAL: {
                /*
                DECIMAL decimal
                 */
                return new DecimalNode(input.readDouble());
            }
            case STRING: {
                /*
                STRING string
                 */
                return new StringNode(input.readUTF());
            }
            case FUNCTION: {
                /*
                FUNCTION name endId (... => parameters) END endId (... => expr) END endId

                FUNCTION_PARAM name modifiers endId (... => typeExpr) END endId [modifiers.hasDefaultValue ? (... => defaultValue) END endId]
                 */
                String name = input.readUTF();
                int endId = input.readInt();

                List<ParameterData> parameterData = new ArrayList<>();

                boolean finished = false;
                while (!finished) {
                    byte b = input.readByte();
                    if (b == 14) {//FUNCTION_PARAM
                        String paramName = input.readUTF();
                        int modifiers = input.readInt();
                        int paramEndId = input.readInt();
                        Expr expr = collectUntil(paramEndId);

                        if ((modifiers & 1) == 0) {
                            parameterData.add(new ParameterData(paramName, expr, null, (modifiers & 2) == 2));
                        } else {
                            Expr defaultValue = collectUntil(paramEndId);
                            parameterData.add(new ParameterData(paramName, expr, defaultValue, (modifiers & 2) == 2));
                        }

                    } else if (b == 0) { //END
                        /*
                        END endId
                         */
                        if (input.readInt() != endId) throw new InvalidBytecodeException("Invalid END");
                        finished = true;
                    } else {
                        throw new InvalidBytecodeException("Invalid Instruction");
                    }
                }

                return new FunctionNode(name.isEmpty() ? null : name, parameterData, collectUntil(endId));
            }
            case IDENTIFIER: {
                /*
                IDENTIFIER parented name [parented ? endId (... => parent) END endId]
                 */
                boolean parented = input.readBoolean();
                String name = input.readUTF();

                if (!parented) {
                    return new Identifier(name);
                }

                int endId = input.readInt();
                return new Identifier(collectUntil(endId), name);
            }
            case INVOCATION: {
                /*
                INVOCATION endId (... => left) END endId (... => arguments) END endId
                 */
                int endId = input.readInt();
                return new Invocation(collectUntil(endId), toList(collectUntil(endId)));
            }
            case UNARY_OP: {
                /*
                UNARY_OP type endId (... => target) END endId
                 */
                int type = input.readInt(), endId = input.readInt();
                return new UnaryOperation(collectUntil(endId), UnaryOperatorType.values()[type]);
            }
            case BINARY_OP: {
                /*
                BINARY_OP type endId (... => left) END endId (... => right) END endId
                 */
                int type = input.readInt(), endId = input.readInt();
                return new BinaryOperation(collectUntil(endId), collectUntil(endId), BinaryOperatorType.values()[type]);
            }
            case SLICE_OP: {
                /*
                SLICE_OP endId (... => obj) END endId (... => start) END endId (... => end) END endId (... => step) END endId
                 */
                int endId = input.readInt();
                return new SliceOperation(collectUntil(endId), collectUntil(endId), collectUntil(endId), collectUntil(endId));
            }
            case GET: {
                /*
                GET endId (... => left) END endId (... => key) END endId
                 */
                int endId = input.readInt();
                return new GetOperation(collectUntil(endId), collectUntil(endId));
            }
            case SET: {
                /*
                SET endId (... => left) END endId (... => key) END endId (... => value) END endId
                 */
                int endId = input.readInt();
                return new SetOperation(collectUntil(endId), collectUntil(endId), collectUntil(endId));
            }
            default: {
                throw new InvalidBytecodeException("Invalid Instruction");
            }
        }
    }

    private List<Expr> toList(Expr expr) {
        if (expr instanceof Statements) {
            return ((Statements) expr).getExprs();
        }

        return Collections.singletonList(expr);
    }
}
