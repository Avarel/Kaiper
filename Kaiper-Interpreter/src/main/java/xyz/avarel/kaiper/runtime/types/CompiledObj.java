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

package xyz.avarel.kaiper.runtime.types;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

public class CompiledObj implements Obj {
    private final CompiledType type;
    private final Scope scope;
    private final CompiledObj parent;

    @SuppressWarnings("unchecked")
    public CompiledObj(CompiledType type, CompiledObj parent, Scope scope) {
        this.type = type;
        this.parent = parent;
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Obj setAttr(String name, Obj value) {
        scope.declare(name, value);
        return Null.VALUE;
    }

    @Override
    public Obj getAttr(String name) {
        Obj obj = scope.directLookup(name);

        if (obj == null) {
            if (parent != null) {
                return parent.getAttr(name);
            }
            return Null.VALUE;
        }

        return obj;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        Obj method = scope.lookup("toString");
        if (method == null) {
            return type.toString() + "$" + hashCode();
        } else {
            return method.invoke(this).toString();
        }
    }
}
