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

package xyz.avarel.aje.runtime.collections;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.modules.Module;
import xyz.avarel.aje.runtime.modules.NativeModule;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.types.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AJE wrapper class for a map.
 */
public class Dictionary extends HashMap<Obj, Obj> implements Obj {
    public static final Type<Dictionary> TYPE = new Type<>("Dictionary");
    public static final Module MODULE = new NativeModule() {{
        declare("TYPE", Dictionary.TYPE);

        declare("size", new NativeFunc("size", "dict") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                return Int.of(((Dictionary) arguments.get(0)).size());
            }
        });
    }};

    /**
     * Creates an empty dictionary.
     */
    public Dictionary() {
        super();
    }

    /**
     * Returns an unmodifiable representation of the map. Note that the map's contents are all converted to
     * their native representation or {@code null} if unable to.
     *
     * @return An unmodifiable representation of the map.
     */
    @Override
    public Map<Object, Object> toJava() {
        Map<Object, Object> map = new HashMap<>();

        for (Entry<Obj, Obj> entry : this.entrySet()) {
            map.put(entry.getKey().toJava(), entry.getValue().toJava());
        }

        return Collections.unmodifiableMap(map);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj get(Obj key) {
        return getOrDefault(key, Undefined.VALUE);
    }

    @Override
    public Obj set(Obj key, Obj value) {
        put(key, value);
        return value;
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "size":
                return Int.of(size());
            default:
                return Obj.super.getAttr(name);
        }
    }
}
