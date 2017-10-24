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
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;

public class PipeForwardParser extends BinaryParser {
    public PipeForwardParser() {
        super(Precedence.INFIX);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
        // left |> right

        throw new UnsupportedOperationException("in progress");

//        Expr right = parser.parseExpr(getPrecedence());
//
//        if (right instanceof Invocation) {
//            Invocation invocation = (Invocation) right;
//            FreeFormStruct argument = invocation.getArgument();
//            Map<String, Expr> elements = new LinkedHashMap<>(argument.getElements());
//
//            if (left instanceof FreeFormStruct) {
//                FreeFormStruct leftTuple = (FreeFormStruct) left;
//
//                if (Collections.disjoint(elements.keySet(), leftTuple.getElements().keySet())) {
//                    elements.putAll(leftTuple.getElements());
//                } else {
//                    throw new SyntaxException("Duplicate tuple field names", left.getPosition());
//                }
//            } else if (elements.put("value", left) != null) {
//                throw new SyntaxException("Duplicate tuple field names", left.getPosition());
//            }
//
//            return new Invocation(
//                    token.getPosition(),
//                    invocation.getLeft(),
//                    new FreeFormStruct(
//                            argument.getPosition(),
//                            elements
//                    )
//            );
//        } else if (right instanceof FunctionNode || right instanceof Identifier) {
//            if (left instanceof FreeFormStruct) {
//                return new Invocation(
//                        token.getPosition(),
//                        right,
//                        (FreeFormStruct) left
//                );
//            } else {
//                return new Invocation(
//                        token.getPosition(),
//                        right,
//                        new FreeFormStruct(
//                                token.getPosition(),
//                                Collections.singletonMap("value", left)
//                        )
//                );
//            }
//        }
//
//        throw new SyntaxException(
//                "Invalid pipe-forward operand " + token.getType(),
//                token.getPosition());
    }
}
