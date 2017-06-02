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

package xyz.avarel.aje.runtime.java;

import org.apache.commons.lang3.ClassUtils;
import xyz.avarel.aje.runtime.*;
import xyz.avarel.aje.runtime.collections.Dictionary;
import xyz.avarel.aje.runtime.collections.Vector;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NativeMapper implements Obj, NativeObject<Object> {
    private final Object real;

    private final Object object; // proxied
    private final String name;

    public NativeMapper(Object object) {
        this(object, null);
    }

    private NativeMapper(Object object, String name) {
        this.real = object;

        if (name != null) {
            try {
                Field field = object.getClass().getField(name);
                object = mapToAJE(field.get(object));
            } catch (NoSuchFieldException | IllegalAccessException e) {}
        }

        this.object = object;

        this.name = name;
    }

    public Object getObject() { // SPECIAL
        return object;
    }

    @Override
    public Type getType() {
        if (object instanceof Obj) return ((Obj) object).getType();

        return new Type("java/" + getObject().getClass().getSimpleName());
    }

    @Override
    public Object toNative() {
        if (object instanceof Obj) return ((Obj) object).toNative();

        return getObject();
    }

    @Override
    public Obj getAttr(String name) { // SPECIAL
        if (object instanceof Obj) return ((Obj) object).getAttr(name);

        return new NativeMapper(object, name);
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (name == null) return Undefined.VALUE;

        List<Object> nativeArgs = arguments.stream()
                .map(Obj::toNative)
                .collect(Collectors.toList());

        List<Class<?>> classes = nativeArgs.stream()
                .map(Object::getClass)
                .collect(Collectors.toList());

        Object result = null;

        outer: for (Method method : real.getClass().getMethods()) {
            if (!method.getName().equals(name)) continue;
            if (classes.size() != method.getParameterCount()) continue;

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < method.getParameterCount(); i++) {

                if (!ClassUtils.isAssignable(classes.get(i), parameterTypes[i])) {
                    continue outer;
                }
            }

            try {
                result = method.invoke(real, nativeArgs.toArray());
            } catch (IllegalAccessException | InvocationTargetException ignore) {}
        }

        return mapToAJE(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (object instanceof Obj) return object.equals(obj);

        if (obj instanceof NativeMapper) {
            return getObject() == ((NativeMapper) obj).getObject();
        }
        return getObject() == obj;
    }

    @Override
    public String toString() {
        if (object instanceof Obj) return object.toString();

        return getObject().toString();
    }

    @Override
    public int hashCode() {
        if (object instanceof Obj) return object.hashCode();

        return getObject().hashCode();
    }

    @SuppressWarnings("unchecked")
    public static Obj mapToAJE(Object result) {
        if (result == null) {
            return Undefined.VALUE;
        } else if (result instanceof Obj) {
            return (Obj) result;
        } else if (result instanceof Integer) {
            return Int.of((Integer) result);
        } else if (result instanceof Double) {
            return Decimal.of((Double) result);
        } else if (result instanceof Boolean) {
            return (Boolean) result ? Bool.TRUE : Bool.FALSE;
        } else if (result instanceof String) {
            return Text.of((String) result);
        } else if (result instanceof List) {
            Vector vector = new Vector();

            ((List<Object>) result).forEach(o -> vector.add(mapToAJE(o)));

            return vector;
        } else if (result instanceof Map) {
            Dictionary dict = new Dictionary();

            ((Map<Object, Object>) result).forEach((key, value) -> dict.put(mapToAJE(key), mapToAJE(value)));

            return dict;
        } else {
            return new NativeMapper(result);
        }
    }
}