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

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.oop.ClassNode;
import xyz.avarel.aje.ast.oop.ConstructorNode;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.AJEParserUtils;
import xyz.avarel.aje.parser.PrefixParser;

import java.util.*;

// class Meme { constructor(x) { this.x = x } }
// class Dank : Meme { constructor(x, y) : super(x) { this.y = y } }

// The most hacky parser thing in existence
public class ClassParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        String className = parser.eat(TokenType.IDENTIFIER).getString();

        ConstructorNode classConstructor = null;

        Identifier classParent = new Identifier("Object");
        if (parser.match(TokenType.COLON)) {
            classParent = parser.parseIdentifier();
        }

        List<FunctionNode> classFunctions = new ArrayList<>();

        if (parser.match(TokenType.LEFT_BRACE)) {
            while (!(parser.nextIs(TokenType.EOF) || parser.nextIs(TokenType.RIGHT_BRACE))) {
                if (parser.match(TokenType.LINE)) continue;

                if (parser.matchSoftKeyword("constructor")) {
                    classConstructor = parseConstructor(parser);
                } else if (parser.nextIs(TokenType.FUNCTION)) {
                    Single expr = parser.parseSingle();
                    if (!(expr instanceof FunctionNode)) {
                        throw new SyntaxException("Internal compiler error");
                    }
                    FunctionNode functionNode = (FunctionNode) expr;
                    functionNode.getParameterExprs().add(0, new ParameterData("this", new Identifier(className)));

                    classFunctions.add(functionNode);
                } else if (parser.matchSoftKeyword("static")) {
                    Single expr = parser.parseSingle();
                    if (!(expr instanceof FunctionNode)) {
                        throw new SyntaxException("Internal compiler error");
                    }
                    FunctionNode functionNode = (FunctionNode) expr;

                    classFunctions.add(functionNode);
                } else {
                    throw new SyntaxException(parser.peek(0).getType()
                            + " is not allowed in the top level of a class declaration",
                            parser.peek(0).getPosition());
                }
            }

            parser.eat(TokenType.RIGHT_BRACE);
        }

        if (classConstructor == null) {
            classConstructor = new ConstructorNode(Collections.emptyList(),
                    Collections.emptyList(),
                    new Statements());
        }

        return new ClassNode(className, classParent, classConstructor, classFunctions);
    }

    private ConstructorNode parseConstructor(AJEParser parser) {
        Set<String> usedParameters = new HashSet<>();

        List<ParameterData> constructorParameters = new ArrayList<>();
        List<Single> constructorSuperExprs = Collections.emptyList();
        Statements constructorExpr = new Statements();

        // constructor(constructorParameters...) : super(superExprs) { constructorExpr }

        // constructorParameters = [var|val] name [: (parameterType)] [= (parameterDefault)]

        if (parser.nextIs(TokenType.LEFT_PAREN)) {
            parser.eat(TokenType.LEFT_PAREN);
            if (!parser.match(TokenType.RIGHT_PAREN)) {
                boolean requireDefault = false;

                do {
                    boolean parameterVariable = parser.match(TokenType.LET);

                    ParameterData parameter = AJEParserUtils.parseParameter(parser, requireDefault);

                    if (parameter.getDefault() != null) {
                        requireDefault = true;
                    }

                    if (usedParameters.contains(parameter.getName())) {
                        throw new SyntaxException("Duplicate parameter name",
                                parser.getLast().getPosition());
                    }

                    if (parameter.isRest() && parser.match(TokenType.COMMA)) {
                        throw new SyntaxException("Rest parameters must be the last parameter",
                                parser.peek(0).getPosition());
                    }

                    constructorParameters.add(parameter);

                    if (parameterVariable) {
                        constructorExpr.getExprs().add(
                                new AssignmentExpr(
                                        AJEParserUtils.THIS_ID,
                                        parameter.getName(),
                                        new Identifier(parameter.getName())
                                )
                        );
                    }

                } while (parser.match(TokenType.COMMA));
                parser.match(TokenType.RIGHT_PAREN);
            }
        }

        if (parser.match(TokenType.COLON)) {
            parser.eatSoftKeyword("super");
            constructorSuperExprs = AJEParserUtils.parseArguments(parser);
        }

        if (parser.nextIs(TokenType.LEFT_BRACE)) {
            constructorExpr = constructorExpr.andThen(AJEParserUtils.parseBlock(parser));
        }

        return new ConstructorNode(constructorParameters,
                constructorSuperExprs,
                constructorExpr);
    }
}
