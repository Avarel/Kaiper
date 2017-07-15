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

import xyz.avarel.aje.VariableFlags;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope[] parents;
    private final Map<String, Obj> map;
    private final Map<String, Short> flagsMap;

    public Scope(Scope... parents) {
        this(new HashMap<>(), new HashMap<>(), parents);
    }

    public Scope(Map<String, Obj> map, Map<String, Short> flagsMap, Scope... parents) {
        this.map = map;
        this.flagsMap = flagsMap;
        this.parents = parents;
    }

    public Obj lookup(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else for (Scope parent : parents) {
            if (parent.contains(key)) {
                return parent.lookup(key);
            }
        }
        return Undefined.VALUE;
    }

    public void declare(String key, Obj value) {
        declare(key, value, (short) 0);
    }

    public void declare(String key, Obj value, short flags) {
        if (map.containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        map.put(key, value);
        flagsMap.put(key, flags);
    }

    public void assign(String key, Obj value) {
        System.out.println(flagsMap.get(key));
        if (map.containsKey(key)) {
            if ((flagsMap.get(key) & VariableFlags.FINAL) == VariableFlags.FINAL) {
                throw new ComputeException("Can not assign to final variable " + key);
            }

            map.put(key, value);
            return;
        } else for (Scope parent : parents) {
            if (parent.contains(key)) {
                parent.assign(key, value);
                return;
            }
        }
        throw new ComputeException(key + " is not defined, it must be declared using Scope#declare first");
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
        return new Scope(new HashMap<>(map), new HashMap<>(flagsMap), parents);
    }

    public Scope subPool() {
        return new Scope(this);
    }

    public Scope combine(Scope otherScope) {
        Scope[] array = Arrays.copyOf(parents, parents.length + 1);
        array[array.length - 1] = otherScope;

        return new Scope(new HashMap<>(map), new HashMap<>(flagsMap), array);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
