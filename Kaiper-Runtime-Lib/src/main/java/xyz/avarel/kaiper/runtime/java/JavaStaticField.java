/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.runtime.java;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.types.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JavaStaticField implements Obj {
    private final JavaType parent;
    private final String name;

    public JavaStaticField(JavaType parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Object getField() {
        if (name != null) {
            try {
                Field field = parent.getWrappedClass().getField(name);
                return field.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }
        return null;
    }


    @Override
    public Type getType() {
        return JavaUtils.mapJavaToKaiperType(getField()).getType();
    }

    @Override
    public Object toJava() {
        Object field = getField();
        if (JavaUtils.hasKaiperTypeEquivalent(field)) {
            return JavaUtils.mapJavaToKaiperType(field).toJava();
        }

        return getField();
    }

    @Override
    public Obj setAttr(String name, Obj value) {
        if (name != null) {
            try {
                Field field = getField().getClass().getField(name);
                Object val = value.toJava();
                field.set(getField(), val);
                return value;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return Null.VALUE;
            }
        }
        return Null.VALUE;
    }

    @Override
    public Obj getAttr(String name) {
        return new JavaField(new JavaObject(JavaUtils.mapJavaToKaiperType(getField())), name);
    }

    @Override
    public Obj invoke(Obj tuple) {
        if (name == null) return Null.VALUE;

        List<Object> nativeArgs;

        if (tuple instanceof Tuple) {
            nativeArgs = new ArrayList<>(tuple.size());
            for (Obj obj : ((Tuple) tuple)._toList()) {
                Object o = obj.toJava();
                nativeArgs.add(o != Null.VALUE ? o : null);
            }
        } else {
            nativeArgs = Collections.singletonList(tuple.toJava());
        }

        List<Class<?>> classes = nativeArgs.stream()
                .map(o -> o != null ? o.getClass() : null)
                .collect(Collectors.toList());

        Object result = null;

        outer:
        for (Method method : parent.getWrappedClass().getMethods()) {
            if (!method.getName().equals(name)) continue;
            if (classes.size() != method.getParameterCount()) continue;

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < method.getParameterCount(); i++) {

                if (!JavaUtils.isAssignable(classes.get(i), parameterTypes[i])) {
                    continue outer;
                }
            }

            try {
                result = method.invoke(null, nativeArgs.toArray());
                break;
            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }
        }

        return JavaUtils.mapJavaToKaiperType(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaStaticField) {
            return getField() == ((JavaStaticField) obj).getField();
        } else if (obj instanceof JavaObject) {
            return getField() == ((JavaObject) obj).getObject();
        }

        Object field = getField();

        if (JavaUtils.hasKaiperTypeEquivalent(field)) {
            return JavaUtils.mapJavaToKaiperType(field).equals(obj);
        }

        return parent.getWrappedClass() == obj;
    }

    @Override
    public String toString() {
        Object field = getField();
        if (JavaUtils.hasKaiperTypeEquivalent(field)) {
            return JavaUtils.mapJavaToKaiperType(field).toString();
        }

        return getField().toString();
    }

    @Override
    public int hashCode() {
        Object field = getField();
        if (JavaUtils.hasKaiperTypeEquivalent(field)) {
            return JavaUtils.mapJavaToKaiperType(field).hashCode();
        }

        return getField().hashCode();
    }

    public JavaType getParent() {
        return parent;
    }
}