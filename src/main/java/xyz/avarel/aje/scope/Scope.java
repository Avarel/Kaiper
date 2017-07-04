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

import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, Obj> map;

    public Scope() {
        this(null);
    }

    public Scope(Scope parent) {
        this(parent, new HashMap<>());
    }

    public Scope(Scope parent, Map<String, Obj> map) {
        this.parent = parent;
        this.map = map;
    }

    public Obj lookup(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else if (parent != null && parent.contains(key)) {
            return parent.lookup(key);
        }
        return Undefined.VALUE;
    }

    public void declare(String key, Obj value) {
        map.put(key, value);
    }

    public void assign(String key, Obj value) {
        if (map.containsKey(key)) {
            map.put(key, value);
        } else if (parent != null) {
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
        return map.containsKey(key) || parent != null && parent.contains(key);
    }

    public Scope copy() {
        return new Scope(this.parent, new HashMap<>(map));
    }

    public Scope subPool() {
        return new Scope(this);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
