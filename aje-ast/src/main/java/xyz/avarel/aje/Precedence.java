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

package xyz.avarel.aje;

/**
 * Default precedence table for the AJE parser.
 */
public class Precedence {
    public static final int DOT = 14;

    /* a(b) */
    public static final int POSTFIX = 13;

    public static final int BLOCK_PARAM = 12;

    /* a ^ b */
    public static final int EXPONENTIAL = 11;

    /* -a | +a | !a | ~a */
    public static final int PREFIX = 10;

    /* a * b | a / b | a % b */
    public static final int MULTIPLICATIVE = 9;

    /* a + b | a - b */
    public static final int ADDITIVE = 8;

    public static final int SHIFT = 7;

    /* a..b */
    public static final int RANGE_TO = 6;

    /* Unused */
    public static final int INFIX = 5;

    /* a > b | a < b | a >= b | a <= b */
    public static final int COMPARISON = 4;

    /* a == b | a != b */
    public static final int EQUALITY = 3;

    /* a && b */
    public static final int CONJUNCTION = 2;

    /* a || b */
    public static final int DISJUNCTION = 1;
}
