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

package xyz.avarel.aje.parser;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.parser.parslets.ElvisParser;
import xyz.avarel.aje.parser.parslets.GetSetParser;
import xyz.avarel.aje.parser.parslets.GroupParser;
import xyz.avarel.aje.parser.parslets.atoms.*;
import xyz.avarel.aje.parser.parslets.flow.ForEachParser;
import xyz.avarel.aje.parser.parslets.flow.IfElseParser;
import xyz.avarel.aje.parser.parslets.flow.ReturnParser;
import xyz.avarel.aje.parser.parslets.functions.*;
import xyz.avarel.aje.parser.parslets.operators.BinaryOperatorParser;
import xyz.avarel.aje.parser.parslets.operators.RangeOperatorParser;
import xyz.avarel.aje.parser.parslets.operators.UnaryOperatorParser;
import xyz.avarel.aje.parser.parslets.variables.AttributeParser;
import xyz.avarel.aje.parser.parslets.variables.DeclarationParser;
import xyz.avarel.aje.parser.parslets.variables.NameParser;
import xyz.avarel.aje.runtime.Obj;

public class DefaultGrammar extends Grammar {
    public static final Grammar INSTANCE = new DefaultGrammar();

    private DefaultGrammar() {
        // BLOCKS
        prefix(TokenType.LEFT_BRACKET, new CollectionsParser());
        prefix(TokenType.LEFT_PAREN, new GroupParser());

        // FLOW CONTROL
        prefix(TokenType.IF, new IfElseParser());
        prefix(TokenType.FOR, new ForEachParser());
        prefix(TokenType.RETURN, new ReturnParser());
        infix(TokenType.ELVIS, new ElvisParser());

        // ATOMS
        prefix(TokenType.INT, new NumberParser());
        prefix(TokenType.DECIMAL, new NumberParser());
        prefix(TokenType.BOOLEAN, new BoolParser());
        prefix(TokenType.TEXT, new TextParser());
        prefix(TokenType.UNDEFINED, new UndefinedParser());


        prefix(TokenType.FUNCTION, new FunctionParser());
        prefix(TokenType.UNDERSCORE, new ImplicitFunctionParser());
        prefix(TokenType.LEFT_BRACE, new LambdaFunctionParser());


        prefix(TokenType.IDENTIFIER, new NameParser());
        prefix(TokenType.VAR, new DeclarationParser());

        // Numeric
        prefix(TokenType.MINUS, new UnaryOperatorParser(Obj::negative));
        prefix(TokenType.PLUS, new UnaryOperatorParser(Obj::identity));
        infix(TokenType.PLUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Obj::plus));
        infix(TokenType.MINUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Obj::minus));
        infix(TokenType.ASTERISK, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::times));
        infix(TokenType.SLASH, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::divide));
        infix(TokenType.PERCENT, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::mod));
        infix(TokenType.CARET, new BinaryOperatorParser(Precedence.EXPONENTIAL, false, Obj::pow));

        // RELATIONAL
        infix(TokenType.EQUALS, new BinaryOperatorParser(Precedence.EQUALITY, true, Obj::isEqualTo));
        infix(TokenType.NOT_EQUAL,
                new BinaryOperatorParser(Precedence.EQUALITY, true, (a, b) -> a.isEqualTo(b).negate()));
        infix(TokenType.GT, new BinaryOperatorParser(Precedence.COMPARISON, true, Obj::greaterThan));
        infix(TokenType.LT, new BinaryOperatorParser(Precedence.COMPARISON, true, Obj::lessThan));
        infix(TokenType.GTE,
                new BinaryOperatorParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.greaterThan(b))));
        infix(TokenType.LTE,
                new BinaryOperatorParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.lessThan(b))));

        // Truth
        prefix(TokenType.BANG, new UnaryOperatorParser(Obj::negate));
        infix(TokenType.AND, new BinaryOperatorParser(Precedence.CONJUNCTION, true, Obj::and));
        infix(TokenType.OR, new BinaryOperatorParser(Precedence.DISJUNCTION, true, Obj::or));

        infix(TokenType.RANGE_TO, new RangeOperatorParser());

        // Functional
        infix(TokenType.LEFT_PAREN, new InvocationParser());
        infix(TokenType.LEFT_BRACE, new BlockParameterParser());

        infix(TokenType.LEFT_BRACKET, new GetSetParser());
        infix(TokenType.DOT, new AttributeParser());
        //register(TokenType.ASSIGN, new AssignmentParser());
        infix(TokenType.PIPE_FORWARD, new PipeForwardParser());
    }
}
