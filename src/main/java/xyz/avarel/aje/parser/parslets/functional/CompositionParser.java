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
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;

import java.util.Arrays;

public class CompositionParser extends BinaryParser {
    public CompositionParser() {
        super(Precedence.PIPE_FORWARD);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        Expr identifier = new Identifier(new Identifier("Function"), "compose");

        switch (token.getType()) { // Compiles down to Function.compose(f1, f2)
            case FORWARD_COMPOSITION: {
                Expr right = parser.parseExpr(getPrecedence());
                return new Invocation(identifier, Arrays.asList(right, left));
            }
            case BACKWARD_COMPOSITION: {
                Expr right = parser.parseExpr(getPrecedence() - 1);
                return new Invocation(identifier, Arrays.asList(left, right));
            }
            default:
                throw new SyntaxException("Illegal token passed into composition parser");
        }
    }
}
