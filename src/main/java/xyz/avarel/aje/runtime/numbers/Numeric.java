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

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Prototype;

public abstract class Numeric extends Number implements Obj {
    public static final Prototype<Numeric> PROTOTYPE = new NumericPrototype();

    @SuppressWarnings("unchecked")
    public static <T> T convert(Obj a, Prototype<T> prototype) {
        if (a instanceof Int) {
            if (prototype == Int.PROTOTYPE) {
                return (T) a;
            } else if (prototype == Decimal.PROTOTYPE) {
                return (T) Decimal.of(((Int) a).value());
            } else if (prototype == Complex.PROTOTYPE) {
                return (T) Complex.of(((Int) a).value());
            }
        } else if (a instanceof Decimal) {
            if (prototype == Int.PROTOTYPE) {
                return (T) Int.of((int) ((Decimal) a).value());
            } else if (prototype == Decimal.PROTOTYPE) {
                return (T) a;
            } else if (prototype == Complex.PROTOTYPE) {
                return (T) Complex.of(((Decimal) a).value());
            }
        } else if (a instanceof Complex) {
            if (prototype == Int.PROTOTYPE) {
                return (T) Int.of((int) ((Complex) a).real());
            } else if (prototype == Decimal.PROTOTYPE) {
                return (T) Decimal.of(((Complex) a).real());
            } else if (prototype == Complex.PROTOTYPE) {
                return (T) a;
            }
        }

        return (T) a;
    }

    public Prototype<Numeric> getType() {
        return PROTOTYPE;
    }

    private static class NumericPrototype extends Prototype<Numeric> {
        public NumericPrototype() {
            super("Number");
        }
    }
}
