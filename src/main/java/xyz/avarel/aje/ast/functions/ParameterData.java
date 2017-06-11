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

package xyz.avarel.aje.ast.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ValueNode;
import xyz.avarel.aje.runtime.Cls;
import xyz.avarel.aje.runtime.Obj;

public class ParameterData {
    private final String name;
    private final Expr type;
    private final Expr defaultExpr;

    public ParameterData(String name) {
        this(name, new ValueNode(null, Obj.CLS), null);
    }

    public ParameterData(Cls cls) {
        this(null, new ValueNode(null, cls), null);
    }

    public ParameterData(String name, Expr type) {
        this(name, type, null);
    }

    public ParameterData(String name, Expr type, Expr defaultExpr) {
        this.name = name;
        this.type = type;
        this.defaultExpr = defaultExpr;
    }

    public String getName() {
        return name;
    }

    public Expr getTypeExpr() {
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
        return sb.toString();
    }
}
