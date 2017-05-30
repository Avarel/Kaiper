/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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
import xyz.avarel.aje.parser.parslets.GetParser;
import xyz.avarel.aje.parser.parslets.GroupParser;
import xyz.avarel.aje.parser.parslets.atoms.BoolParser;
import xyz.avarel.aje.parser.parslets.atoms.NumberParser;
import xyz.avarel.aje.parser.parslets.atoms.UndefinedParser;
import xyz.avarel.aje.parser.parslets.atoms.VectorParser;
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
        register(TokenType.LEFT_BRACKET, new VectorParser());
        register(TokenType.LEFT_PAREN, new GroupParser());

        // FLOW CONTROL
        register(TokenType.IF, new IfElseParser());
        register(TokenType.FOR, new ForEachParser());
        register(TokenType.RETURN, new ReturnParser());

        // ATOMS
        register(TokenType.INT, new NumberParser());
        register(TokenType.DECIMAL, new NumberParser());
        register(TokenType.IMAGINARY, new NumberParser());
        register(TokenType.BOOLEAN, new BoolParser());
        register(TokenType.UNDEFINED, new UndefinedParser());


        register(TokenType.FUNCTION, new FunctionParser());
        register(TokenType.UNDERSCORE, new ImplicitFunctionParser());
        register(TokenType.LEFT_BRACE, new LambdaFunctionParser());


        register(TokenType.IDENTIFIER, new NameParser());
        register(TokenType.VAR, new DeclarationParser());

        // Numeric
        register(TokenType.MINUS, new UnaryOperatorParser(Obj::negative));
        register(TokenType.PLUS, new UnaryOperatorParser(Obj::identity));
        register(TokenType.PLUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Obj::plus));
        register(TokenType.MINUS, new BinaryOperatorParser(Precedence.ADDITIVE, true, Obj::minus));
        register(TokenType.ASTERISK, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::times));
        register(TokenType.SLASH, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::divide));
        register(TokenType.PERCENT, new BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, Obj::mod));
        register(TokenType.CARET, new BinaryOperatorParser(Precedence.EXPONENTIAL, false, Obj::pow));

        // RELATIONAL
        register(TokenType.EQUALS, new BinaryOperatorParser(Precedence.EQUALITY, true, Obj::isEqualTo));
        register(TokenType.NOT_EQUAL, new BinaryOperatorParser(Precedence.EQUALITY, true, (a, b) -> a.isEqualTo(b).negate()));
        register(TokenType.GT, new BinaryOperatorParser(Precedence.COMPARISON, true, Obj::greaterThan));
        register(TokenType.LT, new BinaryOperatorParser(Precedence.COMPARISON, true, Obj::lessThan));
        register(TokenType.GTE, new BinaryOperatorParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.greaterThan(b))));
        register(TokenType.LTE, new BinaryOperatorParser(Precedence.COMPARISON, true, (a, b) -> a.isEqualTo(b).or(a.lessThan(b))));

        // Truth
        register(TokenType.BANG, new UnaryOperatorParser(Obj::negate));
        register(TokenType.AND, new BinaryOperatorParser(Precedence.CONJUNCTION, true, Obj::and));
        register(TokenType.OR, new BinaryOperatorParser(Precedence.DISJUNCTION, true, Obj::or));

        register(TokenType.RANGE_TO, new RangeOperatorParser());

        // Functional
        register(TokenType.LEFT_PAREN, new InvocationParser());
        register(TokenType.LEFT_BRACE, new BlockParameterParser());

        register(TokenType.LEFT_BRACKET, new GetParser());
        register(TokenType.DOT, new AttributeParser());
        //register(TokenType.ASSIGN, new AssignmentParser());
        register(TokenType.PIPE_FORWARD, new PipeForwardParser());
    }
}
