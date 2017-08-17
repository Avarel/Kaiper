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

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope[] parents;
    private final Map<String, Obj> map;

    public Scope(Scope... parents) {
        this(new HashMap<>(), parents);
    }

    public Scope(Map<String, Obj> map, Scope... parents) {
        this.map = map;
        this.parents = parents;
    }

    public Obj directLookup(String key) {
        return map.get(key);
    }

    public Obj get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else for (Scope parent : parents) {
            if (parent.contains(key)) {
                return parent.get(key);
            }
        }
        return null;
    }

    public void declare(String key, Obj value) {
        if (map.containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        map.put(key, value);
    }

    public void assign(String key, Obj value) {
        if (map.containsKey(key)) {
            map.put(key, value);
            return;
        } else for (Scope parent : parents) {
            if (parent.contains(key)) {
                parent.assign(key, value);
                return;
            }
        }
        throw new ComputeException(key + " is not defined, it must be declared first");
    }

    public Scope[] getParents() {
        return parents;
    }

    public Map<String, Obj> getMap() {
        return map;
    }

    public boolean contains(String key) {
        if (map.containsKey(key)) return true;
        for (Scope parent : parents) {
            if (parent.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public Scope copy() {
        return new Scope(new HashMap<>(map), parents);
    }

    public Scope subPool() {
        return new Scope(this);
    }

    public Scope combine(Scope otherScope) {
        Scope[] array = Arrays.copyOf(parents, parents.length + 1);
        array[array.length - 1] = otherScope;

        return new Scope(new HashMap<>(map), array);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
