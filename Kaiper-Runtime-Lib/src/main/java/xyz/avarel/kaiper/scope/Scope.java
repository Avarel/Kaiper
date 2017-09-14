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

import xyz.avarel.kaiper.GenericScope;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Scope extends GenericScope<Obj> {
    public Scope(Scope... parents) {
        this(new HashMap<>(), parents);
    }

    public Scope(Map<String, Obj> map, Scope... parents) {
        super(map, parents);
    }

    public void declare(String key, Obj value) {
        if (getMap().containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        getMap().put(key, value);
    }

    public void assign(String key, Obj value) {
        if (getMap().containsKey(key)) {
            getMap().put(key, value);
            return;
        } else for (Scope parent : getParents()) {
            if (parent.contains(key)) {
                parent.assign(key, value);
                return;
            }
        }
        throw new ComputeException(key + " is not defined, it must be declared first");
    }

    public Scope[] getParents() {
        return (Scope[]) super.getParents();
    }

    @Override
    public Scope copy() {
        return new Scope(new HashMap<>(getMap()), getParents());
    }

    @Override
    public Scope subPool() {
        return new Scope(this);
    }

    @Override
    public Scope combine(GenericScope<Obj> otherScope) {
        Scope[] array = Arrays.copyOf(getParents(), getParents().length + 1);
        array[array.length - 1] = (Scope) otherScope;

        return new Scope(new HashMap<>(getMap()), array);
    }
}
