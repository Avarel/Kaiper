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

package xyz.avarel.kaiper.scope;

import xyz.avarel.kaiper.runtime.*;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.MathModule;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.runtime.types.Type;

public class DefaultScope extends Scope {
    public static final DefaultScope INSTANCE = new DefaultScope();

    private DefaultScope() {
        put("not", DefaultFunctions.NOT.get());
        put("str", DefaultFunctions.STR.get());

        put("Object", Obj.MODULE);
        put("Math", MathModule.INSTANCE);
        put("Tuple", Tuple.MODULE);
        put("Type", Type.MODULE);
        put("Int", Int.MODULE);
        put("Number", Number.MODULE);
        put("Boolean", Bool.MODULE);
        put("Array", Array.MODULE);
        put("Range", Range.MODULE);
        put("Dictionary", Dictionary.MODULE);
        put("String", Str.MODULE);
        put("Function", Func.MODULE);
        put("Undefined", Null.MODULE);
    }

    @Deprecated
    @Override
    public Scope subPool() {
        throw new UnsupportedOperationException();
    }
}
