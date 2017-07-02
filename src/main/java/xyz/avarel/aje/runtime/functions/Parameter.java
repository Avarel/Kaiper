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

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;

public class Parameter {
    private final String name;
    private final Type type;
    private final Expr defaultExpr;

    private Parameter(String name, Type type, Expr defaultExpr) {
        this.name = name;
        this.type = type;
        this.defaultExpr = defaultExpr;
    }

    public static Parameter of(String name) {
        return new Parameter(name, Obj.TYPE, null);
    }

    public static Parameter of(Type type) {
        return new Parameter(null, type, null);
    }

    public static Parameter of(String name, Type type) {
        return new Parameter(name, type, null);
    }

    public static Parameter of(String name, Type type, Expr defaultExpr) {
        return new Parameter(name, type, defaultExpr);
    }


    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean hasDefault() {
        return defaultExpr != null;
    }

    public Expr getDefault() {
        return defaultExpr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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

        if (defaultExpr != null) {
            sb.append(" = ").append(defaultExpr);
        }
        return sb.toString();
    }
}
