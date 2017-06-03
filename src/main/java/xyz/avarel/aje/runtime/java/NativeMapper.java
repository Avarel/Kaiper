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

import xyz.avarel.aje.runtime.NativeObject;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;

public class NativeMapper implements Obj, NativeObject<Object> {
    private final Object object;

    public NativeMapper(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public Type getType() {
        return new Type("java/" + object.getClass().getSimpleName());
    }

    @Override
    public Object toNative() {
        return object;
    }

    @Override
    public Obj getAttr(String name) {
        return new NativeField(object, name);
    }

    @Override
    public boolean equals(Object obj) {
        return object == obj;
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