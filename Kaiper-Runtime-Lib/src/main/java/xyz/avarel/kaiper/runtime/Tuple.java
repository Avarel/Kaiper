/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.runtime;

import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


// Pattern - the anti tuple / destructive
//
// Wildcard
//      _
// matches no matter what
//
// Literal Pattern
//      [], [:], 123, "string"
// Match if equals
//
// Positional Bind Pattern
//      a, b, c
// Matches if the tuple has a _# where # is the position of the field in the tuple
// will also match if the tuple has a field with the same name as it
//
// Field pattern
//      a: (literal or type pattern)
//
// Type Pattern
//      (positional pattern) is (TYPE)
//      a is Int, b is String, c is Array
// Matches/binds only if type matches
//
// Default Pattern
//      (any pattern except default) = (default)
//      a = 2               binds 2 to a if a doesn't match
//      a is Int = 2        binds 2 to a if a isn't Int
// If no matches, bind pattern to default
// Should always return true for a match
//
// Tuples
// 1, 2, 3          is basically _1: 1, _2: 2, _3: 3
// 1, 2, x: 3       is basically _1: 1, _2: 2, x: 3
// x: 2, y: 3       LEGAL
// x: 2, 3          ILLEGAL
//

public class Tuple implements Obj {
    public static final Type<Tuple> TYPE = new Type<>("Tuple");
    public static final Module MODULE = new NativeModule("Tuple") {{
        declare("TYPE", Tuple.TYPE);
    }};

    // String.trim("hello")
    // "hello"::trim

    private final List<Obj> list;

    public Tuple() {
        this(Collections.emptyList());
    }

    public Tuple(Obj value) {
        this(Collections.singletonList(value));
    }

    public Tuple(Obj... values) {
        this(Arrays.asList(values));
    }

    public Tuple(List<Obj> list) {
       this.list = list;
    }

    public int size() {
        return list.size();
    }

    @Override
    public Obj get(Obj key) {
        if (key instanceof Int) {
            return get((Int) key);
        }
        return Null.VALUE;
    }

    private Obj get(Int index) {
        return get(index.value());
    }

    public Obj get(int index) {
        return list.get(index);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        if (list.isEmpty()) {
            return "()";
        }

        StringBuilder builder = new StringBuilder("(");

        Iterator<Obj> iterator = list.iterator();
        while (true) {
            builder.append(iterator.next());

            if (iterator.hasNext()) {
                builder.append(", ");
            } else {
                break;
            }
        }

        builder.append(')');

        return builder.toString();
    }
}
