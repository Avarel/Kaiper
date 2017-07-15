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

package xyz.avarel.aje.interpreter.runtime.types;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.types.Type;
import xyz.avarel.aje.scope.Scope;

public class CompiledObj implements Obj {
    private final Type type;
    private final Scope scope;

    @SuppressWarnings("unchecked")
    public CompiledObj(Type type, Scope scope) {
        this.type = type;
        this.scope = scope;
    }

    @Override
    public Obj setAttr(String name, Obj value) {
        scope.declare(name, value);
        return Undefined.VALUE;
    }

    @Override
    public Obj getAttr(String name) {
        if (scope.contains(name)) {
            return scope.lookup(name);
        }

        return Obj.super.getAttr(name);
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString() + "$" + hashCode();
    }
}
