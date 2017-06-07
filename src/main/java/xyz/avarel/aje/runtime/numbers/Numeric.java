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
import xyz.avarel.aje.runtime.Type;

public abstract class Numeric extends Number implements Obj {
    public static final Type<Numeric> TYPE = new NumericType();

    @SuppressWarnings("unchecked")
    public static <T> T convert(Obj a, Type<T> type) {
        if (a instanceof Int) {
            if (type == Int.TYPE) {
                return (T) a;
            } else if (type == Decimal.TYPE) {
                return (T) Decimal.of(((Int) a).value());
            } else if (type == Complex.TYPE) {
                return (T) Complex.of(((Int) a).value());
            }
        } else if (a instanceof Decimal) {
            if (type == Int.TYPE) {
                return (T) Int.of((int) ((Decimal) a).value());
            } else if (type == Decimal.TYPE) {
                return (T) a;
            } else if (type == Complex.TYPE) {
                return (T) Complex.of(((Decimal) a).value());
            }
        } else if (a instanceof Complex) {
            if (type == Int.TYPE) {
                return (T) Int.of((int) ((Complex) a).real());
            } else if (type == Decimal.TYPE) {
                return (T) Decimal.of(((Complex) a).real());
            } else if (type == Complex.TYPE) {
                return (T) a;
            }
        }

        return (T) a;
    }

    public Type<Numeric> getType() {
        return TYPE;
    }

    private static class NumericType extends Type<Numeric> {
        public NumericType() {
            super("Number");
        }
    }
}
