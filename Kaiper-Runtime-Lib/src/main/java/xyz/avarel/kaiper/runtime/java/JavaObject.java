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

package xyz.avarel.kaiper.runtime.java;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.types.Type;

import java.lang.reflect.Field;

public class JavaObject implements Obj {
    protected final Type<?> type;
    private final Object object;

    public JavaObject(Object object) {
        this.object = object;
        this.type = JavaUtils.JAVA_PROTOTYPES.computeIfAbsent(object.getClass(), JavaType::new);
    }

    public Object getObject() {
        return object;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Object toJava() {
        return object;
    }

    @Override
    public Obj setAttr(String name, Obj value) {
        if (name != null) {
            try {
                Field field = getObject().getClass().getField(name);
                Object val = value.toJava();
                field.set(object, val);
                return value;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return Null.VALUE;
            }
        }
        return Null.VALUE;
    }

    @Override
    public Obj getAttr(String name) {
        return new JavaField(this, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaField) {
            return object.equals(((JavaField) obj).getField());
        } else if (obj instanceof JavaObject) {
            return object.equals(((JavaObject) obj).getObject());
        } else if (obj instanceof JavaType) {
            return object.equals(((JavaType) obj).getWrappedClass());
        } else if (obj instanceof JavaStaticField) {
            return object.equals(((JavaStaticField) obj).getParent().getWrappedClass());
        }
        return object.equals(obj);
    }

    @Override
    public String toString() {
        return object.toString();
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }
}