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

import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Str;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.runtime.types.Type;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

public class JavaUtils {
    /* Apache common langs */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();
    public static Map<Class<?>, Type<?>> JAVA_PROTOTYPES = new WeakHashMap<>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }


    private static final Map<Class<?>, Map<String, PropertyDescriptor>> cache = new WeakHashMap<>();

    static synchronized Map<String, PropertyDescriptor> scanAndCacheBeans(Class<?> c) {
        Map<String, PropertyDescriptor> map = new LinkedHashMap<>();
        cache.put(c, map);

        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(c).getPropertyDescriptors()) {
                map.put(descriptor.getName(), descriptor);
            }
        } catch (IntrospectionException e) {
            //ignore
        }

        return map;
    }

    static synchronized Map<String, PropertyDescriptor> getBeanInfo(Class<?> c) {
        if (cache.containsKey(c)) {
            return cache.get(c);
        }

        return scanAndCacheBeans(c);
    }

    static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }


    static boolean isAssignable(Class<?> cls, final Class<?> toClass) {
        if (toClass == null) {
            return false;
        }
        // have to check for null, as isAssignableFrom doesn't
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        //autoboxing:
        if (cls.isPrimitive() && !toClass.isPrimitive()) {
            cls = primitiveToWrapper(cls);
            if (cls == null) {
                return false;
            }
        }

        if (toClass.isPrimitive() && !cls.isPrimitive()) {
            cls = wrapperToPrimitive(cls);
            if (cls == null) {
                return false;
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass)
                        || Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // should never get here
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }

    static boolean hasKaiperTypeEquivalent(Object result) {
        return result == null
                || result instanceof Integer
                || result instanceof Double
                || result instanceof Boolean
                || result instanceof String
                || result instanceof List
                || result instanceof Map;
    }

    @SuppressWarnings("unchecked")
    static Obj mapJavaToKaiperType(Object result) {
        if (result == null) {
            return Null.VALUE;
        } else if (result instanceof Obj) {
            return (Obj) result;
        } else if (result instanceof Integer) {
            return Int.of((Integer) result);
        } else if (result instanceof Double) {
            return Number.of((Double) result);
        } else if (result instanceof Boolean) {
            return Bool.of((Boolean) result);
        } else if (result instanceof String) {
            return Str.of((String) result);
        } else if (result instanceof List) {
            Array array = new Array();
            for (Object o : ((List<Object>) result)) {
                array.add(mapJavaToKaiperType(o));
            }
            return array;
        } else if (result instanceof Map) {
            Dictionary dict = new Dictionary();

            Map<Object, Object> map = (Map<Object, Object>) result;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                dict.put(mapJavaToKaiperType(entry.getKey()), mapJavaToKaiperType(entry.getValue()));
            }

            return dict;
        } else if (result instanceof Class) {
            return JAVA_PROTOTYPES.computeIfAbsent(((Class) result), JavaType::new);
        } else {
            return new JavaObject(result);
        }
    }
}
