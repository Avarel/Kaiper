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

package xyz.avarel.aje.runtime;

import xyz.avarel.aje.scope.Scope;

@SuppressWarnings("unused")
public class Cls<T> implements Obj<Cls> {
    private final Cls parent;
    private final String name;
    private Scope scope;

    public Cls(String name) {
        this(Obj.CLS, name);
    }

    public Cls(Cls parent, String name) {
        this.scope = parent != null ? parent.scope.subPool() : new Scope();
        this.parent = parent;
        this.name = name;
    }

    public boolean is(Cls cls) {
        Cls t = this;
        do {
            if (t.equals(cls)) return true;
            t = t.parent;
        } while (t != null);
        return false;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Obj getAttr(String name) {
        return scope.lookup(name);
    }

    @Override
    public Cls getType() {
        return this;
    }

    @Override
    public Cls toJava() {
        return this;
    }

    public Cls getParent() {

        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toExtendedString() {
        if (parent != null) {
            return name + ": " + parent;
        } else {
            return name;
        }
    }
}