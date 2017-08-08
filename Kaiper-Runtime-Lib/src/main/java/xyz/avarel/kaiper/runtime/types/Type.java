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

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;

@SuppressWarnings("unused")
public class Type<T> implements Obj {
    public static final Type<Type> TYPE = new Type<Type>("Type") {
        @Override
        public Type getParent() {
            return Obj.TYPE;
        }
    };

    private final Type parent;
    private final String name;
    private final Constructor constructor;

    public Type(String name) {
        this(name, null);
    }

    public Type(String name, Constructor constructor) {
        this(Obj.TYPE, name, constructor);
    }

    public Type(Type parent, String name) {
        this(parent, name, null);
    }

    public Type(Type parent, String name, Constructor constructor) {
        this.parent = parent;
        this.name = name;
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


    public Constructor getConstructor() {
        return constructor;
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
    public Obj invoke(Tuple arguments) {
        // fixme
        if (constructor == null) {
            throw new ComputeException(toString() + " does not support instantiation");
        } else {
            return constructor.invoke(arguments);
        }
    }
}