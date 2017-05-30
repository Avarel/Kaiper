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

import java.util.Collections;

public class PipeForwardParser extends BinaryParser {
    public PipeForwardParser() {
        super(Precedence.PIPE_FORWARD);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence());

        if (right instanceof InvocationExpr) {
            ((InvocationExpr) right).getArguments().add(0, left);
            return right;
        } else if (right instanceof FunctionAtom || right instanceof Identifier) {
            return new InvocationExpr(token.getPosition(), right, Collections.singletonList(left));
        }

        throw new SyntaxException("Pipe-forward requires the right operand to be either: invocation, function, or name", token.getPosition());
    }
}
