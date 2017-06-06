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

package xyz.avarel.aje.scope;

import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Text;
import xyz.avarel.aje.runtime.collections.Dictionary;
import xyz.avarel.aje.runtime.collections.Range;
import xyz.avarel.aje.runtime.collections.Vector;
import xyz.avarel.aje.runtime.functions.AJEFunction;
import xyz.avarel.aje.runtime.numbers.Complex;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

public class DefaultScope extends Scope {
    public static final DefaultScope INSTANCE = new DefaultScope();

    private DefaultScope() {
        declare("pi", Decimal.of(Math.PI));
        declare("e", Decimal.of(Math.E));
        declare("i", Complex.of(0, 1));

        declare("compose", DefaultFunctions.COMPOSE.get());

        declare("sqrt", DefaultFunctions.SQUARE_ROOT.get());
        declare("cbrt", DefaultFunctions.CUBE_ROOT.get());
        declare("exp", DefaultFunctions.EXPONENTIAL.get());
        declare("log", DefaultFunctions.LOG10.get());
        declare("ln", DefaultFunctions.LOG_NATURAL.get());
        declare("round", DefaultFunctions.ROUND.get());
        declare("floor", DefaultFunctions.FLOOR.get());
        declare("ceil", DefaultFunctions.CEILING.get());
        declare("sum", DefaultFunctions.SUM.get());
        declare("product", DefaultFunctions.PRODUCT.get());
        declare("factorial", DefaultFunctions.FACTORIAL.get());

        declare("sin", DefaultFunctions.SINE.get());
        declare("cos", DefaultFunctions.COSINE.get());
        declare("tan", DefaultFunctions.TANGENT.get());
        declare("csc", DefaultFunctions.COSECANT.get());
        declare("sec", DefaultFunctions.SECANT.get());
        declare("cot", DefaultFunctions.COTANGENT.get());

        declare("sinh", DefaultFunctions.HYPERBOLIC_SINE.get());
        declare("cosh", DefaultFunctions.HYPERBOLIC_COSINE.get());
        declare("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.get());
        declare("csch", DefaultFunctions.HYPERBOLIC_COSECANT.get());
        declare("sech", DefaultFunctions.HYPERBOLIC_SECANT.get());
        declare("coth", DefaultFunctions.HYPERBOLIC_COTANGENT.get());

        declare("asin", DefaultFunctions.ARCSINE.get());
        declare("acos", DefaultFunctions.ARCCOSINE.get());
        declare("atan", DefaultFunctions.ARCTANGENT.get());
        declare("acsc", DefaultFunctions.ARCCOSECANT.get());
        declare("asec", DefaultFunctions.ARCSECANT.get());
        declare("acot", DefaultFunctions.ARCCOTANGENT.get());
        declare("atan2", DefaultFunctions.ARCTANGENT2.get());

        declare("forEach", DefaultFunctions.FOREACH.get());
        declare("each", DefaultFunctions.FOREACH.get());
        declare("map", DefaultFunctions.MAP.get());
        declare("filter", DefaultFunctions.FILTER.get());
        declare("fold", DefaultFunctions.FOLD.get());

        // Types
        declare("Int", Int.TYPE);
        declare("Decimal", Decimal.TYPE);
        declare("Complex", Complex.TYPE);
        declare("Number", Numeric.TYPE);
        declare("Object", Obj.TYPE);
        declare("Boolean", Bool.TYPE);
        declare("Vector", Vector.TYPE);
        declare("Range", Range.TYPE);
        declare("Dictionary", Dictionary.TYPE);
        declare("String", Text.TYPE);
        declare("Function", AJEFunction.TYPE);
    }

    @Deprecated
    @Override
    public Scope subPool() {
        throw new UnsupportedOperationException();
    }
}
