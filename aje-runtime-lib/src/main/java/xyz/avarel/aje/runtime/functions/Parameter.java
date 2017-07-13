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

package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;

public class Parameter {
    private final String name;
    private final Type type;
    private final Obj defaultObj;
    private final boolean rest;

    private Parameter(String name, Type type, Obj defaultObj, boolean rest) {
        this.name = name;
        this.type = type;
        this.defaultObj = defaultObj;
        this.rest = rest;
    }

    public static Parameter of(String name) {
        return Parameter.of(name, Obj.TYPE, null, false);
    }

    public static Parameter of(Type type) {
        return Parameter.of(type, false);
    }

    public static Parameter of(Type type, boolean rest) {
        return new Parameter(null, type, null, rest);
    }

    public static Parameter of(String name, Type type) {
        return Parameter.of(name, type, null, false);
    }

    public static Parameter of(String name, Type type, Obj defaultExpr, boolean rest) {
        return new Parameter(name, type, defaultExpr, rest);
    }


    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean hasDefault() {
        return defaultObj != null;
    }

    public Obj getDefault() {
        return defaultObj;
    }

    public boolean isRest() {
        return rest;
    }

    public String typeString() {
        return isRest() ? "..." + getType().getName() : getType().getName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (rest) {
            sb.append("...");
        }

        if (name != null) {
            sb.append(name);
        }

        if (name != null) {
            if (type != Obj.TYPE) {
                sb.append(": ");
                sb.append(type);
            }
        } else {
            sb.append(type);
        }

        if (defaultObj != null) {
            sb.append(" = ").append(defaultObj);
        }
        return sb.toString();
    }
}
