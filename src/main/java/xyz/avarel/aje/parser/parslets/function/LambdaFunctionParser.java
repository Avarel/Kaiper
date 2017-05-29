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

package xyz.avarel.aje.parser.parslets.function;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LambdaFunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Parameter> parameters = new ArrayList<>();

        if (!parser.match(TokenType.ARROW)) {
            Set<String> paramNames = new HashSet<>();

            do {
                String parameterName = parser.eat(TokenType.IDENTIFIER).getText();

                if (paramNames.contains(parameterName)) {
                    throw new SyntaxException("Duplicate parameter names", parser.getLast().getPosition());
                } else {
                    paramNames.add(parameterName);
                }

                Expr parameterType = new ValueAtom(parser.peek(0).getPosition(), Obj.TYPE);

                if (parser.match(TokenType.COLON)) {
                    Token typeToken = parser.eat(TokenType.IDENTIFIER);
                    parameterType = new NameAtom(typeToken.getPosition(), typeToken.getText());
                    while (parser.match(TokenType.DOT)) {
                        parameterType = new NameAtom(typeToken.getPosition(), parameterType, parser.eat(TokenType.IDENTIFIER).getText());
                    }
                }

                Parameter parameter = new Parameter(parameterName, parameterType);
                parameters.add(parameter);
            } while (parser.match(TokenType.COMMA));

            parser.eat(TokenType.ARROW);
        }

        Expr expr = parser.parseStatements();

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionAtom(token.getPosition(), parameters, expr);
    }
}
