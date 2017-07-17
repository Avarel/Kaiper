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

package xyz.avarel.aje.runtime;

import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.List;

public enum DefaultFunctions {
    STR(new NativeFunc("str", Parameter.of("a")) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj obj = arguments.get(0);
            if (obj instanceof Str) {
                return obj;
            }
            return Str.of(obj.toString());
        }
    }),

    NOT(new NativeFunc("not", Parameter.of("function")) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            return new NativeFunc("not") {
                @Override
                protected Obj eval(List<Obj> arguments0) {
                    return arguments.get(0).invoke(arguments0).negate();
                }
            };
        }
    });

    private final NativeFunc function;

    DefaultFunctions(NativeFunc function) {
        this.function = function;
    }

    public NativeFunc get() {
        return function;
    }
}
