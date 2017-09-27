/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.parser.parslets.functional;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PipeForwardParser extends BinaryParser {
    public PipeForwardParser() {
        super(Precedence.INFIX);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function invocation are disabled");
        }

        // left |> right

        Single right = parser.parseSingle(getPrecedence());

        if (right instanceof Invocation) {
            Invocation invocation = (Invocation) right;
            Single argument = invocation.getArgument();

            if (argument instanceof TupleExpr) {
                TupleExpr tuple = (TupleExpr) argument;
                Map<String, Single> elements = new LinkedHashMap<>(tuple.getElements());

                if (left instanceof TupleExpr) {
                    TupleExpr leftTuple = (TupleExpr) left;

                    if (Collections.disjoint(elements.keySet(), leftTuple.getElements().keySet())) {
                        elements.putAll(leftTuple.getElements());
                    } else {
                        throw new SyntaxException("Duplicate tuple field names", left.getPosition());
                    }
                } else {
                    if (elements.put("value", left) != null) {
                        throw new SyntaxException("Duplicate tuple field names", left.getPosition());
                    }
                }

                return new Invocation(
                        token.getPosition(),
                        invocation.getLeft(),
                        new TupleExpr(
                                tuple.getPosition(),
                                elements
                        )
                );
            }
        } else if (right instanceof FunctionNode || right instanceof Identifier) {
            if (left instanceof TupleExpr) {
                return new Invocation(
                        token.getPosition(),
                        right,
                        (TupleExpr) left
                );
            } else {
                return new Invocation(
                        token.getPosition(),
                        right,
                        new TupleExpr(
                                token.getPosition(),
                                Collections.singletonMap(
                                        "value",
                                        left
                                )
                        )
                );
            }
        }

        throw new SyntaxException(
                "Invalid pipe-forward operand " + token.getType(),
                token.getPosition());
    }
}
