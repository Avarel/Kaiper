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
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.InfixParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ImplicitFunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        ParserProxy ip = new ParserProxy(parser, token);

        Expr expr = ip.parseInfix(0, new Identifier(token.getPosition(), token.getString()));

        List<Parameter> list = new ArrayList<>();

        for (String param : ip.parameters) {
            list.add(new Parameter(param));
        }

        return new FunctionAtom(token.getPosition(), list, expr);
    }

    private static final class ParserProxy extends AJEParser {
        private final Set<String> parameters = new LinkedHashSet<>();
        private final AJEParser proxy;
        private AJEParser current;

        private ParserProxy(AJEParser proxy, Token token) {
            super(proxy);

            this.proxy = proxy;
            this.current = this;
            this.parameters.add(token.getString());
        }

        @Override
        public Token eat() {
            Token token = super.eat();
            if (token.getType() == TokenType.UNDERSCORE) {
                parameters.add(token.getString());
                return new Token(token.getPosition(), TokenType.IDENTIFIER, token.getString());
            }
            return token;
        }

        @Override
        public Expr parseInfix(int precedence, Expr left) {
            while (precedence < getPrecedence()) {
                Token token = eat();

                if (token.getType() == TokenType.PIPE_FORWARD) {
                    current = proxy;
                }

                InfixParser infix = getInfixParsers().get(token.getType());

                if (infix == null) throw new SyntaxException("Could not parse token `" + token.getString() + "`", token.getPosition());

                left = infix.parse(current, left, token);
            }
            return left;
        }
    }
}
