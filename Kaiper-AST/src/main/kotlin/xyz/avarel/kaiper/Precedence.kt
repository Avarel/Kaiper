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
package xyz.avarel.kaiper

/**
 * Default precedence table for the Kaiper xyz.avarel.kaiper.parser.
 */
object Precedence {
    val DOT = 16

    /* :: */
    val REF = 15

    /* a(b) */
    val POSTFIX = 14

    /* a ^ b */
    val EXPONENTIAL = 13

    /* -a | +a | !a | ~a */
    val PREFIX = 12

    /* a * b | a / b | a % b */
    val MULTIPLICATIVE = 11

    /* a + b | a - b */
    val ADDITIVE = 10

    val SHIFT = 9

    /* is | |> */
    val INFIX = 8

    /* a > b | a < b | a >= b | a <= b */
    val COMPARISON = 7

    /* a == b | a != b */
    val EQUALITY = 6

    /* a && b */
    val CONJUNCTION = 5

    /* a: b */
    val FREEFORM_STRUCT = 3

    /* a, b */
    val TUPLE = 2

    /* a || b */
    val DISJUNCTION = 4

    val ASSIGNMENT = 1
}
