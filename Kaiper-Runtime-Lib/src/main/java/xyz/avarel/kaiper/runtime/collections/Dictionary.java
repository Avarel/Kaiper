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

package xyz.avarel.kaiper.runtime.collections;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Kaiper wrapper class for a map.
 */
public class Dictionary implements Obj, Map<Obj, Obj> {
    public static final Type<Dictionary> TYPE = new Type<>("Dictionary");
    public static final Module MODULE = new NativeModule("Dictionary") {{
        declare("TYPE", Dictionary.TYPE);

        declare("size", new NativeFunc("size", "dict") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                return Int.of(arguments.get("dict").size());
            }
        });
    }};
    private final Map<Obj, Obj> map;

    /**
     * Creates an empty dictionary.
     */
    public Dictionary() {
        this(new HashMap<>());
    }

    public Dictionary(Map<Obj, Obj> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Obj get(Object key) {
        return map.get(key);
    }

    @Override
    public Obj put(Obj key, Obj value) {
        return map.put(key, value);
    }

    @Override
    public Obj remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Obj, ? extends Obj> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Obj> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Obj> values() {
        return map.values();
    }

    @Override
    public Set<Entry<Obj, Obj>> entrySet() {
        return map.entrySet();
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

        for (Map.Entry<Obj, Obj> entry : this.map.entrySet()) {
            map.put(entry.getKey().toJava(), entry.getValue().toJava());
        }

        return map;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj get(Obj key) {
        return map.getOrDefault(key, Null.VALUE);
    }

    @Override
    public Obj set(Obj key, Obj value) {
        map.put(key, value);
        return value;
    }

    @Override
    public String toString() {
        return map.toString();
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
