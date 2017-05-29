/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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

package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class Statements extends Expr {
    private final Expr before;
    private final Expr after;

    private boolean hasNext;

    public Statements(Expr before, Expr after) {
        super(null);

        this.before = before;
        this.after = after;

        if (before instanceof Statements) {
            ((Statements) before).hasNext = true;
        }
    }

    public Expr getBefore() {
        return before;
    }

    public Expr getAfter() {
        return after;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        before.ast(builder, indent, false);
        builder.append('\n');
        after.ast(builder, indent, !hasNext);
    }

    @Override
    public String toString() {
        return "statements";
    }
}
