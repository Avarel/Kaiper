package xyz.avarel.kaiper;

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

import java.util.HashMap;
import java.util.Map;

public abstract class GenericScope<T> {
    private final GenericScope<T>[] parents;
    private final Map<String, T> map;

    @SafeVarargs
    public GenericScope(GenericScope<T>... parents) {
        this(new HashMap<>(), parents);
    }

    @SafeVarargs
    public GenericScope(Map<String, T> map, GenericScope<T>... parents) {
        this.map = map;
        this.parents = parents;
    }

    public T directLookup(String key) {
        return map.get(key);
    }

    public T get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else for (GenericScope<T> parent : parents) {
            if (parent.contains(key)) {
                return parent.get(key);
            }
        }
        return null;
    }

    public GenericScope<T>[] getParents() {
        return parents;
    }

    public Map<String, T> getMap() {
        return map;
    }

    public boolean contains(String key) {
        if (map.containsKey(key)) return true;
        for (GenericScope<T> parent : parents) {
            if (parent.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public abstract GenericScope<T> copy();
    public abstract GenericScope<T> subPool();
    public abstract GenericScope<T> combine(GenericScope<T> otherScope);

    @Override
    public String toString() {
        return map.toString();
    }
}
