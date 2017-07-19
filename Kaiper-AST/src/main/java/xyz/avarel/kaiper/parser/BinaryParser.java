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

package xyz.avarel.kaiper.parser;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;

public abstract class BinaryParser implements InfixParser {
    private final int precedence;
    private final boolean leftAssoc;

    public BinaryParser(int precedence) {
        this(precedence, true);
    }

    public BinaryParser(int precedence, boolean leftAssoc) {
        this.precedence = precedence;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (!(left instanceof Single)) {
            throw new SyntaxException("Internal compiler error", token.getPosition());
        }
        return parse(parser, (Single) left, token);
    }

    public abstract Expr parse(AJEParser parser, Single left, Token token);

    @Override
    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}