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

package xyz.avarel.aje.parser.parslets.oop;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.oop.ClassNode;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;

import java.util.*;

public class ClassParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        List<FunctionNode> functions = new ArrayList<>();

        FunctionNode constructorNode = null;

        Expr parent = new Identifier("Object");
        if (parser.matchSoftKeyword("extends")) {
            parent = parser.parseExpr();
        }

        if (parser.match(TokenType.LEFT_BRACE)) {
            while (!parser.nextIs(TokenType.EOF) && !parser.nextIs(TokenType.RIGHT_BRACE)) {
                FunctionNode functionNode;

                if (parser.match(TokenType.LINE)) {
                    continue;
                }

                if (parser.nextIs(TokenType.FUNCTION)) {
                    Expr node = parser.parseExpr();
                    if (!(node instanceof FunctionNode)) {
                        throw new SyntaxException("Class members can only be functions",
                                parser.getLast().getPosition());
                    }

                    ((FunctionNode) node).getParameterExprs().add(0, new ParameterData("self", new Identifier(name)));
                    functionNode = (FunctionNode) node;
                } else if (parser.matchSoftKeyword("static")) {
                    Expr node = parser.parseExpr();
                    if (!(node instanceof FunctionNode)) {
                        throw new SyntaxException("Class members can only be functions",
                                parser.getLast().getPosition());
                    }

                    functionNode = (FunctionNode) node;
                } else if (parser.matchSoftKeyword("constructor")) {
                    if (constructorNode != null) {
                        throw new SyntaxException("Only one constructor per class", parser.getLast().getPosition());
                    }
                    constructorNode = parseConstructor(parser, name);
                    continue;
                } else {
                    throw new SyntaxException("Class members can only be functions", parser.peek(0).getPosition());
                }

                functions.add(functionNode);
            }
            parser.eat(TokenType.RIGHT_BRACE);
        }

        if (constructorNode == null) {
            constructorNode = new FunctionNode(
                    "new",
                    Collections.singletonList(new ParameterData("self", new Identifier(name))),
                    new Statements());
        }

        functions.add(constructorNode);

        return new ClassNode(name, parent, functions);
    }

    public FunctionNode parseConstructor(AJEParser parser, String name) {
        List<ParameterData> parameters = new ArrayList<>();

        parameters.add(new ParameterData("self", new Identifier(name)));

        parser.eat(TokenType.LEFT_PAREN);
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            Set<String> paramNames = new HashSet<>();
            boolean requireDef = false;

            do {
                boolean rest = parser.match(TokenType.REST);

                String parameterName = parser.eat(TokenType.IDENTIFIER).getString();

                if (paramNames.contains(parameterName)) {
                    throw new SyntaxException("Duplicate parameter name", parser.getLast().getPosition());
                } else {
                    paramNames.add(parameterName);
                }

                Expr parameterType = new Identifier("Object");
                Expr parameterDefault = null;

                if (parser.match(TokenType.COLON)) {
                    parameterType = parser.parseExpr(Precedence.POSTFIX - 1);
                }

                if (parser.match(TokenType.ASSIGN)) {
                    parameterDefault = parser.parseExpr();
                    requireDef = true;
                } else if (requireDef) {
                    throw new SyntaxException("All parameters after the first default requires a default",
                            parser.peek(0).getPosition());
                }

                ParameterData parameter = new ParameterData(parameterName, parameterType, parameterDefault, rest);
                parameters.add(parameter);

                if (rest && parser.match(TokenType.COMMA)) {
                    throw new SyntaxException("Rest parameters must be the last parameter",
                            parser.peek(0).getPosition());
                }
            } while (parser.match(TokenType.COMMA));
            parser.match(TokenType.RIGHT_PAREN);
        }

        Expr expr;

        if (parser.match(TokenType.ASSIGN)) {
            expr = parser.parseExpr();
        } else {
            parser.eat(TokenType.LEFT_BRACE);
            if (parser.match(TokenType.RIGHT_BRACE)) {
                expr = UndefinedNode.VALUE;
            } else {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            }
        }

        return new FunctionNode("new", parameters, expr);
    }
}
