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

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class KaiperClassInvocationHandler implements InvocationHandler {
    private static final AtomicInteger indexer = new AtomicInteger();
    private final Map<String, Obj> map;
    private final int i;

    public KaiperClassInvocationHandler(Dictionary dictionary) {
        this.i = indexer.getAndIncrement();
        this.map = new HashMap<>();

        for (Map.Entry<Obj, Obj> entry : dictionary.entrySet()) {
            map.put(entry.getKey().toJava().toString(), entry.getValue());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (!map.containsKey(methodName)) {
            if (method.getName().equals("toString") && method.getParameterCount() == 0) {
                Class<?>[] interfaces = proxy.getClass().getInterfaces();
                if (interfaces.length == 1) {
                    return interfaces[0].getName() + "$" + i + "@" + Integer.toHexString(System.identityHashCode(proxy)) + map;
                }
                return "xyz.avarel.kaiper.runtime.java.KaiperProxy$" + i + "@" + Integer.toHexString(System.identityHashCode(proxy)) + Arrays.toString(interfaces) + map;
            }

            if (method.getName().equals("hashCode") && method.getParameterCount() == 0) {
                return System.identityHashCode(proxy);
            }

            if (method.getName().equals("equals") && method.getParameterCount() == 1) {
                return args[0] == proxy;
            }

            throw new UnsupportedOperationException(method.toString());
        }


        Obj obj = map.get(methodName);
        Class<?> returnType = method.getReturnType();
        if (obj.getClass().isAssignableFrom(returnType)) return obj;
        Object toJava = obj.toJava();
        if (toJava.getClass().isAssignableFrom(returnType)) return toJava;

        Obj returnObj;
        if (args != null) { // todo not converted to tuple
            returnObj = obj.invoke(JavaUtils.mapJavaToKaiperType(args).as(Array.TYPE));
        } else {
            returnObj = obj.invoke(Collections.emptyList());
        }

        if (returnObj.getClass().isAssignableFrom(returnType)) return returnObj;
        Object toJava1 = returnObj.toJava();
        if (toJava1.getClass().isAssignableFrom(returnType)) return toJava1;

        if (returnType.equals(Void.TYPE) || returnType.equals(Void.class)) {
            return null;
        }

        throw new UnsupportedOperationException(method.toString());
    }
}
