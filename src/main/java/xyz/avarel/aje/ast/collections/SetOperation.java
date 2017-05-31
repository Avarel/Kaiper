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

package xyz.avarel.aje.ast.collections;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class SetOperation extends GetOperation {
    private final Expr left;
    private final Expr key;
    private final Expr expr;

    public SetOperation(Position position, Expr left, Expr key, Expr expr) {
        super(position, left, key);
        this.left = left;
        this.key = key;
        this.expr = expr;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getKey() {
        return key;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("get\n");
        left.ast("target", builder, indent + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        key.ast("key", builder, indent + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "set";
    }
}
