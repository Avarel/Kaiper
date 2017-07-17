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

        declare("Object", Obj.TYPE);
        declare("Type", Type.TYPE);
        declare("Int", Int.TYPE);
        declare("Number", Number.TYPE);
        declare("Boolean", Bool.TYPE);
        declare("Array", Array.TYPE);
        declare("Range", Range.TYPE);
        declare("Dictionary", Dictionary.TYPE);
        declare("String", Str.TYPE);
        declare("Function", Func.TYPE);
        declare("Undefined", Undefined.TYPE);

        declare("Math", MathModule.INSTANCE);
        declare("Ints", Int.MODULE);
        declare("Decimals", Number.MODULE);
        declare("Arrays", Array.MODULE);
        declare("Ranges", Range.MODULE);
        declare("Dictionaries", Dictionary.MODULE);
        declare("Strings", Str.MODULE);
    }

    @Deprecated
    @Override
    public Scope subPool() {
        throw new UnsupportedOperationException();
    }
}
