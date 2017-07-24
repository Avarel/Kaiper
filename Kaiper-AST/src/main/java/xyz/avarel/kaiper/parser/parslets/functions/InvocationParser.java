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

package xyz.avarel.kaiper.parser.parslets.functions;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

import java.util.ArrayList;
import java.util.List;

public class InvocationParser extends BinaryParser {
    public InvocationParser() {
        super(Precedence.POSTFIX);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        List<Single> arguments = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(parser.parseSingle());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_PAREN);
        }

        return new Invocation(token.getPosition(), left, arguments);
    }
}
