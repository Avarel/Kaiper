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
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;

import java.util.Collections;

public class PipeBackwardParser extends BinaryParser {
    public PipeBackwardParser() {
        super(Precedence.INFIX);
    }

    @Override
    public Expr parse(AJEParser parser, Single left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function invocation are disabled");
        }

        Single right = parser.parseSingle(getPrecedence() - 1);

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
}
