/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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

package xyz.avarel.aje.parser.parslets.functions;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.functions.FunctionAtom;
import xyz.avarel.aje.ast.invocation.InvocationExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockParameterParser extends BinaryParser {
    public BlockParameterParser() {
        super(Precedence.INVOCATION);
    }

    @Override // [1..10] |> fold(0) { a, b -> a + b }
    public Expr parse(AJEParser parser, Expr left, Token token) { // [1..10] |> filter { it -> it % 2 == 0 }
        Expr block = parser.getPrefixParsers().get(token.getType()).parse(parser, token);

        if (left instanceof InvocationExpr) {
            ((InvocationExpr) left).getArguments().add(block);
        } else if (left instanceof FunctionAtom || left instanceof Identifier) {
            List<Expr> args = new ArrayList<>();
            args.add(block);
            return new InvocationExpr(token.getPosition(), left, args);
        }

        throw new SyntaxException("Block parameters incompatible with " + left.getClass().getSimpleName(), token.getPosition());
    }
}
