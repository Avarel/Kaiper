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

import xyz.avarel.aje.runtime.*;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.collections.Dictionary;
import xyz.avarel.aje.runtime.collections.Range;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.MathModule;
import xyz.avarel.aje.runtime.numbers.Number;
import xyz.avarel.aje.runtime.types.Type;

public class DefaultScope extends Scope {
    public static final DefaultScope INSTANCE = new DefaultScope();

    private DefaultScope() {
        declare("not", DefaultFunctions.NOT.get());
        declare("str", DefaultFunctions.STR.get());

        declare("Object", Obj.MODULE);
        declare("Math", MathModule.INSTANCE);
        declare("Type", Type.MODULE);
        declare("Int", Int.MODULE);
        declare("Number", Number.MODULE);
        declare("Boolean", Bool.MODULE);
        declare("Array", Array.MODULE);
        declare("Range", Range.MODULE);
        declare("Dictionary", Dictionary.MODULE);
        declare("String", Str.MODULE);
        declare("Function", Func.MODULE);
        declare("Undefined", Undefined.MODULE);
    }

    @Deprecated
    @Override
    public Scope subPool() {
        throw new UnsupportedOperationException();
    }
}
