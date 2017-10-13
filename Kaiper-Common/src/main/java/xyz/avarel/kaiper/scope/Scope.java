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

package xyz.avarel.kaiper.scope;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// TODO Javadocs
// TODO inherit map
// inheritable map
public class Scope<K, V> {
    private final Scope<K, V>[] parents;
    private final Map<K, V> map;

    @SafeVarargs
    public Scope(Scope<K, V>... parents) {
        this(new HashMap<>(), parents);
    }

    @SafeVarargs
    public Scope(Map<K, V> map, Scope<K, V>... parents) {
        this.map = map;
        this.parents = parents;
    }

    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            return value;
        } else for (Scope<K, V> parent : parents) {
            V value0 = parent.get(key);
            if (value0 != null) {
                return value0;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V remove(K key) {
        return map.remove(key);
    }

    public Scope<K, V>[] getParents() {
        return parents;
    }

    public Map<K, V> getMap() {
        return map;
    }

    public boolean contains(K key) {
        if (map.containsKey(key)) return true;
        for (Scope<K,V> parent : parents) {
            if (parent.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public Scope<K, V> copy() {
        return new Scope<>(new HashMap<>(map), parents);
    }

    public Scope<K, V> subScope() {
        return new Scope<>(this);
    }

    public Scope<K, V> copyWithParent(Scope<K, V> otherScope) {
        Scope<K, V>[] array = Arrays.copyOf(parents, parents.length + 1);
        array[array.length - 1] = otherScope;

        return new Scope<>(new HashMap<>(map), array);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}