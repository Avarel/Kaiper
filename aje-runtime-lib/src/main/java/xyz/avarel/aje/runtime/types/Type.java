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

package xyz.avarel.aje.runtime.types;

import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;
import xyz.avarel.aje.scope.ScopeContainer;

import java.util.List;

@SuppressWarnings("unused")
public class Type<T> implements Obj<Type>, ScopeContainer {
    public static final Type<Type> TYPE = new TypeObj();

    private final Type parent;
    private final String name;
    private final Scope scope;
    private final Constructor constructor;

    public Type(String name) {
        this(name, null);
    }

    public Type(String name, Constructor constructor) {
        this(Obj.TYPE, name, constructor);
    }

    public Type(Type parent, String name) {
        this(parent, name, parent != null ? parent.scope.subPool() : new Scope(), null);
    }

    public Type(Type parent, String name, Constructor constructor) {
        this(parent, name, parent != null ? parent.scope.subPool() : new Scope(), constructor);
    }

    public Type(Type parent, String name, Scope scope, Constructor constructor) {
        this.parent = parent;
        this.name = name;
        this.scope = scope;
        this.constructor = constructor;
        if (constructor != null) constructor.targetType = this;
    }

    public boolean is(Type type) {
        Type t = this;
        do {
            if (t.equals(type)) return true;
            t = t.getParent();
        } while (t != null);
        return false;
    }

    public Scope getScope() {
        return scope;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    @Override
    public Obj getAttr(String name) {
        return scope.lookup(name);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Type toJava() {
        return this;
    }

    public Type getParent() {
        return parent;
    }

    public boolean hasParent() {
        return getParent() != null;
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

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (constructor == null) {
            throw new ComputeException(toString() + " does not support instantiation");
        } else {
            return constructor.invoke(arguments);
        }
    }

    private static class TypeObj extends Type<Type> {
        public TypeObj() {
            super("Type");


        }

        @Override
        public Type getParent() {
            return Obj.TYPE;
        }
    }
}