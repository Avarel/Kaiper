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
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;


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

    private final Map<String, Obj> map;

    public Tuple() {
        this(Collections.emptyMap());
    }

    public Tuple(Obj value) {
        this(Collections.singletonMap("value", value));
    }

    public Tuple(Map<String, Obj> map) {
       this.map = map;
    }

    public boolean hasAttr(String name) {
        return map.containsKey(name);
    }

    @Override
    public Obj getAttr(String name) {
        Obj obj = map.get(name);
        return obj == null ? Obj.super.getAttr(name) : obj;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        if (map.isEmpty()) {
            return "()";
        }

        StringBuilder builder = new StringBuilder();


        Iterator<Map.Entry<String, Obj>> iterator = map.entrySet().iterator();
        while (true) {
            Map.Entry<String, Obj> entry = iterator.next();

            builder.append(entry.getKey());
            builder.append(": ");

            if (entry.getValue() instanceof Tuple) {
                builder.append('(');
                builder.append(entry.getValue());
                builder.append(')');
            } else {
                builder.append(entry.getValue());
            }

            if (iterator.hasNext()) {
                builder.append(", ");
            } else {
                break;
            }
        }

        return builder.toString();
    }
}
