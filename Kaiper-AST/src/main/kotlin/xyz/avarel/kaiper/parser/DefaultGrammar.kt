/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.parser

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.operations.BinaryOperatorType
import xyz.avarel.kaiper.operations.UnaryOperatorType
import xyz.avarel.kaiper.parser.parslets.*
import xyz.avarel.kaiper.parser.parslets.flow.ForEachParser
import xyz.avarel.kaiper.parser.parslets.flow.IfElseParser
import xyz.avarel.kaiper.parser.parslets.flow.MatchParser
import xyz.avarel.kaiper.parser.parslets.flow.ReturnParser
import xyz.avarel.kaiper.parser.parslets.functional.InfixInvocationParser
import xyz.avarel.kaiper.parser.parslets.functional.InvocationParser
import xyz.avarel.kaiper.parser.parslets.functional.PipeForwardParser
import xyz.avarel.kaiper.parser.parslets.functional.ReferenceParser
import xyz.avarel.kaiper.parser.parslets.functions.FunctionParser
import xyz.avarel.kaiper.parser.parslets.nodes.*
import xyz.avarel.kaiper.parser.parslets.operators.BinaryOperatorParser
import xyz.avarel.kaiper.parser.parslets.operators.UnaryOperatorParser
import xyz.avarel.kaiper.parser.parslets.tuples.TupleColonParser
import xyz.avarel.kaiper.parser.parslets.tuples.TupleCommaParser
import xyz.avarel.kaiper.parser.parslets.variables.*

class DefaultGrammar private constructor() : Grammar() {

    init {
        // BLOCKS
        prefix(TokenType.LEFT_BRACKET, CollectionsParser())
        prefix(TokenType.LEFT_PAREN, GroupParser())

        // TUPLE
        infix(TokenType.COMMA, TupleCommaParser())
        infix(TokenType.COLON, TupleColonParser())

        // FLOW CONTROL
        prefix(TokenType.MATCH, MatchParser())
        prefix(TokenType.IF, IfElseParser())
        prefix(TokenType.FOR, ForEachParser())
        prefix(TokenType.RETURN, ReturnParser())
        infix(TokenType.ELVIS, ElvisParser())

        // NODES
        prefix(TokenType.INT, IntParser())
        prefix(TokenType.NUMBER, DecimalParser())
        prefix(TokenType.BOOLEAN, BoolParser())
        prefix(TokenType.STRING, StrParser())
        prefix(TokenType.NULL, NullParser())


        prefix(TokenType.FUNCTION, FunctionParser())

        prefix(TokenType.IDENTIFIER, NameParser())
        prefix(TokenType.LET, DeclarationParser())

        infix(TokenType.ASSIGN, AssignmentParser())
        infix(TokenType.OPTIONAL_ASSIGN, OptAssignmentParser())

        prefix(TokenType.TYPE, TypeParser())
        prefix(TokenType.MODULE, ModuleParser())

        // Numeric
        prefix(TokenType.MINUS, UnaryOperatorParser(UnaryOperatorType.MINUS))
        prefix(TokenType.PLUS, UnaryOperatorParser(UnaryOperatorType.PLUS))
        infix(TokenType.PLUS, BinaryOperatorParser(Precedence.ADDITIVE, true, BinaryOperatorType.PLUS))
        infix(TokenType.MINUS, BinaryOperatorParser(Precedence.ADDITIVE, true, BinaryOperatorType.MINUS))
        infix(TokenType.ASTERISK, BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, BinaryOperatorType.TIMES))
        infix(TokenType.SLASH, BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, BinaryOperatorType.DIVIDE))
        infix(TokenType.PERCENT, BinaryOperatorParser(Precedence.MULTIPLICATIVE, true, BinaryOperatorType.MODULUS))
        infix(TokenType.CARET, BinaryOperatorParser(Precedence.EXPONENTIAL, false, BinaryOperatorType.POWER))
        infix(TokenType.SHIFT_RIGHT, BinaryOperatorParser(Precedence.SHIFT, true, BinaryOperatorType.SHR))
        infix(TokenType.SHIFT_LEFT, BinaryOperatorParser(Precedence.SHIFT, true, BinaryOperatorType.SHL))

        // RELATIONAL
        infix(TokenType.EQUALS, BinaryOperatorParser(Precedence.EQUALITY, true, BinaryOperatorType.EQUALS))
        infix(TokenType.NOT_EQUAL, BinaryOperatorParser(Precedence.EQUALITY, true, BinaryOperatorType.NOT_EQUALS))
        infix(TokenType.GT, BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.GREATER_THAN))
        infix(TokenType.LT, BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.LESS_THAN))
        infix(TokenType.GTE, BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.GREATER_THAN_EQUAL))
        infix(TokenType.LTE, BinaryOperatorParser(Precedence.COMPARISON, true, BinaryOperatorType.LESS_THAN_EQUAL))

        // Truth
        prefix(TokenType.BANG, UnaryOperatorParser(UnaryOperatorType.NEGATE))
        infix(TokenType.AND, BinaryOperatorParser(Precedence.CONJUNCTION, true, BinaryOperatorType.AND))
        infix(TokenType.OR, BinaryOperatorParser(Precedence.DISJUNCTION, true, BinaryOperatorType.OR))

        //        infix(TokenType.IS, new BinaryOperatorParser(Precedence.INFIX, true, BinaryOperatorType.IS));

        // Functional
        infix(TokenType.LEFT_PAREN, InvocationParser())
        infix(TokenType.REF, ReferenceParser())
        infix(TokenType.IDENTIFIER, InfixInvocationParser())

        infix(TokenType.LEFT_BRACKET, GetSetParser())
        infix(TokenType.DOT, MemberParser())

        //        infix(TokenType.PIPE_BACKWARD, new PipeBackwardParser());
        infix(TokenType.PIPE_FORWARD, PipeForwardParser())
    }

    companion object {
        val INSTANCE: Grammar = DefaultGrammar()
    }
}
