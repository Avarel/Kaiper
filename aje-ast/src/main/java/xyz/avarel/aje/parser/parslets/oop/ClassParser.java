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

// class Meme { constructor(x) { self.x = x } }
// class Dank extends Meme { constructor(x, y) : super(x) { self.y = y } }

public class ClassParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        List<ParameterData> constructorParameters = new ArrayList<>();
        Statements constructorExpr = new Statements();
        List<Expr> constructorSuperExprs = Collections.emptyList();


        if (parser.nextIs(TokenType.LEFT_PAREN)) {

            parser.eat(TokenType.LEFT_PAREN);
            if (!parser.match(TokenType.RIGHT_PAREN)) {
                Set<String> paramNames = new HashSet<>();
                boolean requireDef = false;

                do {
                    boolean var = parser.match(TokenType.VAR);

                    ParameterData parameter = AJEParserUtils.parseParameter(parser, requireDef);

                    if (parameter.getDefault() != null) {
                        requireDef = true;
                    }

                    if (paramNames.contains(parameter.getName())) {
                        throw new SyntaxException("Duplicate parameter name", parser.getLast().getPosition());
                    } else {
                        paramNames.add(parameter.getName());
                    }

                    if (parameter.isRest() && parser.match(TokenType.COMMA)) {
                        throw new SyntaxException("Rest parameters must be the last parameter",
                                parser.peek(0).getPosition());
                    }

                    constructorParameters.add(parameter);

                    if (var) {
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

        Expr parent = new Identifier("Object");
        if (parser.match(TokenType.COLON)) {
            parent = parser.parseExpr(100);

            if (parser.nextIs(TokenType.LEFT_PAREN)) {
                constructorSuperExprs = AJEParserUtils.parseArguments(parser);
            }
        }

        List<FunctionNode> functions = new ArrayList<>();

        if (parser.match(TokenType.LEFT_BRACE)) {
            while (!(parser.nextIs(TokenType.EOF) || parser.nextIs(TokenType.RIGHT_BRACE))) {
                if (parser.match(TokenType.LINE)) continue;

                if (parser.matchSoftKeyword("init")) {
                    constructorExpr.getExprs().add(AJEParserUtils.parseBlock(parser));
                } else {
                    throw new SyntaxException(parser.peek(0)
                            .getType() + " is not allowed in the top level of a class declaration");
                }
            }

            parser.eat(TokenType.RIGHT_BRACE);
        }

        ConstructorNode constructorNode = new ConstructorNode(constructorParameters,
                constructorSuperExprs,
                constructorExpr);

        return new ClassNode(name, parent, constructorNode, functions);
    }


}
