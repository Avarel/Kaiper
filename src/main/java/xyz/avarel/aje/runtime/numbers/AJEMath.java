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

package xyz.avarel.aje.runtime.numbers;

import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.scope.DefaultFunctions;

public class AJEMath {
    public static final Type TYPE = new MathType();

    private AJEMath() {}

    private static class MathType extends Type {
        public MathType() {
            super("Math");

            getScope().declare("PI", Decimal.of(Math.PI));
            getScope().declare("E", Decimal.of(Math.E));

            getScope().declare("sqrt", DefaultFunctions.SQUARE_ROOT.get());
            getScope().declare("cbrt", DefaultFunctions.CUBE_ROOT.get());
            getScope().declare("exp", DefaultFunctions.EXPONENTIAL.get());
            getScope().declare("log", DefaultFunctions.LOG10.get());
            getScope().declare("ln", DefaultFunctions.LOG_NATURAL.get());
            getScope().declare("round", DefaultFunctions.ROUND.get());
            getScope().declare("floor", DefaultFunctions.FLOOR.get());
            getScope().declare("ceil", DefaultFunctions.CEILING.get());
            getScope().declare("sum", DefaultFunctions.SUM.get());
            getScope().declare("product", DefaultFunctions.PRODUCT.get());
            getScope().declare("factorial", DefaultFunctions.FACTORIAL.get());
            getScope().declare("random", DefaultFunctions.RANDOM.get());

            getScope().declare("sin", DefaultFunctions.SINE.get());
            getScope().declare("cos", DefaultFunctions.COSINE.get());
            getScope().declare("tan", DefaultFunctions.TANGENT.get());
            getScope().declare("csc", DefaultFunctions.COSECANT.get());
            getScope().declare("sec", DefaultFunctions.SECANT.get());
            getScope().declare("cot", DefaultFunctions.COTANGENT.get());

            getScope().declare("sinh", DefaultFunctions.HYPERBOLIC_SINE.get());
            getScope().declare("cosh", DefaultFunctions.HYPERBOLIC_COSINE.get());
            getScope().declare("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.get());
            getScope().declare("csch", DefaultFunctions.HYPERBOLIC_COSECANT.get());
            getScope().declare("sech", DefaultFunctions.HYPERBOLIC_SECANT.get());
            getScope().declare("coth", DefaultFunctions.HYPERBOLIC_COTANGENT.get());

            getScope().declare("asin", DefaultFunctions.ARCSINE.get());
            getScope().declare("acos", DefaultFunctions.ARCCOSINE.get());
            getScope().declare("atan", DefaultFunctions.ARCTANGENT.get());
            getScope().declare("acsc", DefaultFunctions.ARCCOSECANT.get());
            getScope().declare("asec", DefaultFunctions.ARCSECANT.get());
            getScope().declare("acot", DefaultFunctions.ARCCOTANGENT.get());
            getScope().declare("atan2", DefaultFunctions.ARCTANGENT2.get());
        }
    }
}
