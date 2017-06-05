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

    public Parameter(String name) {
        this(name, Obj.TYPE, null);
    }

    public Parameter(Type type) {
        this(null, type, null);
    }

    public Parameter(String name, Type type) {
        this(name, type, null);
    }

    public Parameter(String name, Type type, Expr defaultExpr) {
        this.name = name;
        this.type = type;
        this.defaultExpr = defaultExpr;
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
            sb.append(": ");
        }
        sb.append(type);

        if (defaultExpr != null) {
            sb.append(" = ").append(defaultExpr);
        }
        return sb.toString();
    }
}
