package xyz.avarel.kaiper.runtime.java;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Undefined;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.types.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.stream.Collectors;

public class JavaType extends Type<JavaObject> {
    private final Class<?> clazz;

    public JavaType(Class<?> clazz) {
        super(clazz.getSimpleName());
        this.clazz = clazz;
    }

    public Class<?> getWrappedClass() {
        return clazz;
    }

    @Override
    public Obj setAttr(String name, Obj value) {
        if (name != null) {
            try {
                Field field = getWrappedClass().getField(name);
                Object val = value.toJava();
                field.set(null, val);
                return null;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return Undefined.VALUE;
            }
        }
        return Undefined.VALUE;
    }

    @Override
    public Obj getAttr(String name) {
        return new JavaStaticField(this, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaField) {
            return clazz.equals(((JavaField) obj).getField());
        } else if (obj instanceof JavaObject) {
            return clazz.equals(((JavaObject) obj).getObject());
        } else if (obj instanceof JavaType) {
            return clazz.equals(((JavaType) obj).getWrappedClass());
        } else if (obj instanceof JavaStaticField) {
            return clazz.equals(((JavaStaticField) obj).getParent().getWrappedClass());
        }
        return clazz.equals(obj);
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (Proxy.isProxyClass(clazz)) {
            return new JavaObject(Proxy.newProxyInstance(
                    clazz.getClassLoader(), clazz.getInterfaces(),
                    new KaiperClassInvocationHandler(arguments.get(0).as(Dictionary.TYPE))
            ));
        }

        if (clazz.isInterface()) {
            return new JavaObject(Proxy.newProxyInstance(
                    clazz.getClassLoader(), new Class[]{clazz},
                    new KaiperClassInvocationHandler(arguments.get(0).as(Dictionary.TYPE))
            ));
        }

        List<Object> nativeArgs = arguments.stream()
                .map(Obj::toJava)
                .collect(Collectors.toList());

        List<Class<?>> classes = nativeArgs.stream()
                .map(Object::getClass)
                .collect(Collectors.toList());

        Object result = null;

        outer:
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (classes.size() != constructor.getParameterCount()) continue;

            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < constructor.getParameterCount(); i++) {

                if (!JavaUtils.isAssignable(classes.get(i), parameterTypes[i])) {
                    continue outer;
                }
            }

            try {
                result = constructor.newInstance(nativeArgs.toArray());
                break;
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new ComputeException(e);
            }
        }

        return JavaUtils.mapJavaToKaiperType(result);
    }

}
