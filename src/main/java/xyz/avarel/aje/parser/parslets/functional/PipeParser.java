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

package xyz.avarel.aje.parser.parslets.functional;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;

import java.util.Collections;

public class PipeParser extends BinaryParser {
    public PipeParser() {
        super(Precedence.PIPE_FORWARD);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function invocation are disabled");
        }

        switch (token.getType()) {
            case PIPE_FORWARD: {
                Expr right = parser.parseExpr(getPrecedence());

                if (right instanceof Invocation) {
                    ((Invocation) right).getArguments().add(0, left);
                    return right;
                } else if (right instanceof FunctionNode || right instanceof Identifier) {
                    return new Invocation(right, Collections.singletonList(left));
                }

                throw new SyntaxException(
                        "Pipe-forward requires the right operand to be either: invocation, function, or name",
                        token.getPosition());
            }
            case PIPE_BACKWARD: {
                Expr right = parser.parseExpr(getPrecedence() - 1);

                if (left instanceof Invocation) {
                    ((Invocation) left).getArguments().add(0, left);
                    return left;
                } else if (left instanceof FunctionNode || left instanceof Identifier) {
                    return new Invocation(left, Collections.singletonList(right));
                }

                throw new SyntaxException(
                        "Pipe-backward requires the left operand to be either: invocation, function, or name",
                        token.getPosition());
            }
            default:
                throw new SyntaxException("Illegal token passed into pipe parser");
        }
    }
}
