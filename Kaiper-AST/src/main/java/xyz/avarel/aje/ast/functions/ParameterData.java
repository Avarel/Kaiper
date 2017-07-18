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

import xyz.avarel.aje.ast.Single;

public class ParameterData {
    private final String name;
    private final Single defaultExpr;
    private final boolean rest;

    public ParameterData(String name) {
        this(name, null, false);
    }

    public ParameterData(String name, Single defaultExpr, boolean rest) {
        this.name = name;
        this.defaultExpr = defaultExpr;
        this.rest = rest;
    }

    public String getName() {
        return name;
    }

    public Single getDefault() {
        return defaultExpr;
    }

    public boolean isRest() {
        return rest;
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
