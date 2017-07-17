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

import xyz.avarel.aje.runtime.DefaultFunctions;
import xyz.avarel.aje.runtime.modules.NativeModule;

public class MathModule extends NativeModule {
    public static MathModule INSTANCE = new MathModule();

    private MathModule() {
        declare("PI", Number.of(Math.PI));
        declare("E", Number.of(Math.E));

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
        declare("random", DefaultFunctions.RANDOM.get());

        declare("sin", DefaultFunctions.SINE.get());
        declare("cos", DefaultFunctions.COSINE.get());
        declare("tan", DefaultFunctions.TANGENT.get());

        declare("sinh", DefaultFunctions.HYPERBOLIC_SINE.get());
        declare("cosh", DefaultFunctions.HYPERBOLIC_COSINE.get());
        declare("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.get());

        declare("asin", DefaultFunctions.ARCSINE.get());
        declare("acos", DefaultFunctions.ARCCOSINE.get());
        declare("atan", DefaultFunctions.ARCTANGENT.get());
        declare("atan2", DefaultFunctions.ARCTANGENT2.get());
    }
}
