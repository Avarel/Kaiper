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

package xyz.avarel.kaiper;

/**
 * Default precedence table for the Kaiper parser.
 */
public class Precedence {
    public static final int DOT = 16;

    /* a(b) */
    public static final int POSTFIX = 15;

    /* a ^ b */
    public static final int EXPONENTIAL = 14;

    /* -a | +a | !a | ~a */
    public static final int PREFIX = 13;

    /* a * b | a / b | a % b */
    public static final int MULTIPLICATIVE = 12;

    /* a + b | a - b */
    public static final int ADDITIVE = 11;

    public static final int SHIFT = 10;

    /* a..b */
    public static final int RANGE_TO = 9;

    /* is */
    public static final int INFIX = 8;

    /* a > b | a < b | a >= b | a <= b */
    public static final int COMPARISON = 7;

    /* a == b | a != b */
    public static final int EQUALITY = 6;

    /* a && b */
    public static final int CONJUNCTION = 5;

    /* a || b */
    public static final int DISJUNCTION = 4;

    /* a: b */
    public static final int TUPLE_PAIR = 3;

    /* a, b */
    public static final int TUPLE = 2;

    public static final int ASSIGNMENT = 1;
}
