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

    private final Obj[] values;

    public Tuple() {
        this(new Obj[0]);
    }

    public Tuple(Obj value) {
        this(new Obj[] { value });
    }

    public Tuple(Obj... values) {
        this.values = values;
    }

    public Tuple(List<Obj> values) {
       this.values = values.toArray(new Obj[values.size()]);
    }

    public int size() {
        return values.length;
    }

    // INTERNAL
    public List<Obj> asList() {
        return Arrays.asList(values);
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
        return values[index];
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        return Arrays.equals(values, tuple.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        if (values.length == 0) {
            return "()";
        }

        StringBuilder builder = new StringBuilder("(");

        for (int i = 0; i < values.length - 1; i++) {
            builder.append(values[i]).append(", ");
        }

        builder.append(values[values.length - 1]);

        builder.append(')');

        return builder.toString();
    }
}
