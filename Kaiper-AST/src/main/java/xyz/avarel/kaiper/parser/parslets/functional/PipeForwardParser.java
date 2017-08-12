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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PipeForwardParser extends BinaryParser {
    public PipeForwardParser() {
        super(Precedence.INFIX);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function invocation are disabled");
        }

        Single right = parser.parseSingle(getPrecedence());

        if (right instanceof Invocation) {
            Invocation invocation = (Invocation) right;
            Single argument = invocation.getArgument();

            if (argument instanceof TupleExpr) {
                TupleExpr tuple = (TupleExpr) argument;

                List<Single> unnamedElements = new ArrayList<>(tuple.getUnnamedElements());
                unnamedElements.add(0, left);

                return new Invocation(
                        token.getPosition(),
                        invocation.getLeft(),
                        new TupleExpr(
                                argument.getPosition(),
                                unnamedElements,
                                tuple.getNamedElements()
                        )
                );
            } else {
                List<Single> unnamedElements = new ArrayList<>(2);
                unnamedElements.add(left);
                unnamedElements.add(argument);
                return new Invocation(
                        token.getPosition(),
                        invocation.getLeft(),
                        new TupleExpr(
                                argument.getPosition(),
                                unnamedElements,
                                Collections.emptyMap()
                        )
                );
            }
        } else if (right instanceof FunctionNode || right instanceof Identifier) {
            return new Invocation(token.getPosition(), right, left);
        }

        throw new SyntaxException(
                "Pipe-forward requires the right operand to be either: invocation, function, or name",
                token.getPosition());
    }
}
