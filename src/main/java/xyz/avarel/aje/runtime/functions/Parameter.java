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
import xyz.avarel.aje.runtime.Prototype;

public class Parameter {
    private final String name;
    private final Prototype prototype;
    private final Expr defaultExpr;

    private Parameter(String name, Prototype prototype, Expr defaultExpr) {
        this.name = name;
        this.prototype = prototype;
        this.defaultExpr = defaultExpr;
    }

    public static Parameter of(String name) {
        return new Parameter(name, Obj.PROTOTYPE, null);
    }

    public static Parameter of(Prototype prototype) {
        return new Parameter(null, prototype, null);
    }

    public static Parameter of(String name, Prototype type) {
        return new Parameter(name, type, null);
    }

    public static Parameter of(String name, Prototype prototype, Expr defaultExpr) {
        return new Parameter(name, prototype, defaultExpr);
    }


    public String getName() {
        return name;
    }

    public Prototype getPrototype() {
        return prototype;
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
            if (prototype != Obj.PROTOTYPE) {
                sb.append(": ");
                sb.append(prototype);
            }
        } else {
            sb.append(prototype);
        }

        if (defaultExpr != null) {
            sb.append(" = ").append(defaultExpr);
        }
        return sb.toString();
    }
}
