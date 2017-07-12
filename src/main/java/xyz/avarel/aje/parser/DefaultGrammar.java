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
import xyz.avarel.aje.ast.operations.BinaryOperatorType;
import xyz.avarel.aje.ast.operations.UnaryOperatorType;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.parslets.ElvisParser;
import xyz.avarel.aje.parser.parslets.GetSetParser;
import xyz.avarel.aje.parser.parslets.GroupParser;
import xyz.avarel.aje.parser.parslets.flow.ForEachParser;
import xyz.avarel.aje.parser.parslets.flow.IfElseParser;
import xyz.avarel.aje.parser.parslets.flow.ReturnParser;
import xyz.avarel.aje.parser.parslets.functional.CompositionParser;
import xyz.avarel.aje.parser.parslets.functional.PipeParser;
import xyz.avarel.aje.parser.parslets.functions.*;
import xyz.avarel.aje.parser.parslets.nodes.*;
import xyz.avarel.aje.parser.parslets.operators.BinaryOperatorParser;
import xyz.avarel.aje.parser.parslets.operators.RangeOperatorParser;
import xyz.avarel.aje.parser.parslets.operators.UnaryOperatorParser;
import xyz.avarel.aje.parser.parslets.variables.AttributeParser;
import xyz.avarel.aje.parser.parslets.variables.DeclarationParser;
import xyz.avarel.aje.parser.parslets.variables.NameParser;

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

        // NODES
        prefix(TokenType.INT, new IntParser());
        prefix(TokenType.DECIMAL, new DecimalParser());
        prefix(TokenType.BOOLEAN, new BoolParser());
        prefix(TokenType.TEXT, new TextParser());
        prefix(TokenType.UNDEFINED, new UndefinedParser());
//        prefix(TokenType.ATOM, new AtomParser());

        prefix(TokenType.FUNCTION, new FunctionParser());
        prefix(TokenType.UNDERSCORE, new ImplicitFunctionParser());
        prefix(TokenType.LEFT_BRACE, new LambdaFunctionParser());


        prefix(TokenType.IDENTIFIER, new NameParser());
        prefix(TokenType.LET, new DeclarationParser());

        // Numeric
        prefix(TokenType.MINUS, new UnaryOperatorParser(UnaryOperatorType.MINUS));
        prefix(TokenType.PLUS, new UnaryOperatorParser(UnaryOperatorType.PLUS));
        infix(TokenType.PLUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, BinaryOperatorType.PLUS));
        infix(TokenType.MINUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, BinaryOperatorType.MINUS));
        infix(TokenType.ASTERISK, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, BinaryOperatorType.TIMES));
        infix(TokenType.SLASH, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, BinaryOperatorType.DIVIDE));
        infix(TokenType.PERCENT, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, BinaryOperatorType.MODULUS));
        infix(TokenType.CARET, new BinaryOperatorParser(Precedence.EXPONENTIAL, false, BinaryOperatorType.POWER));

        // RELATIONAL
        infix(TokenType.EQUALS, new BinaryOperatorParser(Precedence.EQUALITY, true, BinaryOperatorType.EQUALS));
        infix(TokenType.NOT_EQUAL, new BinaryOperatorParser(Precedence.EQUALITY, true, BinaryOperatorType.NOT_EQUALS));
        infix(TokenType.GT, new BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.GREATER_THAN));
        infix(TokenType.LT, new BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.LESS_THAN));
        infix(TokenType.GTE,
                new BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.GREATER_THAN_EQUAL));
        infix(TokenType.LTE, new BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.LESS_THAN_EQUAL));

        // Truth
        prefix(TokenType.BANG, new UnaryOperatorParser(UnaryOperatorType.NEGATE));
        infix(TokenType.AND, new BinaryOperatorParser(Precedence.CONJUNCTION, true, BinaryOperatorType.AND));
        infix(TokenType.OR, new BinaryOperatorParser(Precedence.DISJUNCTION, true, BinaryOperatorType.OR));

        infix(TokenType.RANGE_TO, new RangeOperatorParser());

        // Functional
        infix(TokenType.LEFT_PAREN, new InvocationParser());
        infix(TokenType.LEFT_BRACE, new BlockParameterParser());

        infix(TokenType.LEFT_BRACKET, new GetSetParser());
        infix(TokenType.DOT, new AttributeParser());
        //register(TokenType.ASSIGN, new AssignmentParser());
        PipeParser pipeParser = new PipeParser();
        infix(TokenType.PIPE_BACKWARD, pipeParser);
        infix(TokenType.PIPE_FORWARD, pipeParser);

        CompositionParser compositionParser = new CompositionParser();
        infix(TokenType.BACKWARD_COMPOSITION, compositionParser);
        infix(TokenType.FORWARD_COMPOSITION, compositionParser);
    }
}
