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
import xyz.avarel.aje.runtime.Cls;
import xyz.avarel.aje.runtime.Obj;

public class Parameter {
    private final String name;
    private final Cls cls;
    private final Expr defaultExpr;

    private Parameter(String name, Cls cls, Expr defaultExpr) {
        this.name = name;
        this.cls = cls;
        this.defaultExpr = defaultExpr;
    }

    public static Parameter of(String name) {
        return new Parameter(name, Obj.CLS, null);
    }

    public static Parameter of(Cls cls) {
        return new Parameter(null, cls, null);
    }

    public static Parameter of(String name, Cls type) {
        return new Parameter(name, type, null);
    }

    public static Parameter of(String name, Cls cls, Expr defaultExpr) {
        return new Parameter(name, cls, defaultExpr);
    }


    public String getName() {
        return name;
    }

    public Cls getCls() {
        return cls;
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
            if (cls != Obj.CLS) {
                sb.append(": ");
                sb.append(cls);
            }
        } else {
            sb.append(cls);
        }

        if (defaultExpr != null) {
            sb.append(" = ").append(defaultExpr);
        }
        return sb.toString();
    }
}
