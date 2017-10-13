/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.ast.flow;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;

import java.util.List;

public class Statements extends Expr {
    private final List<Expr> statements;

    public Statements(List<Expr> statements) {
        super(statements.get(0).getPosition());
        this.statements = statements;
    }

    public List<Expr> getExprs() {
        return statements;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        for (int i = 0; i < statements.size() - 1; i++) {
            statements.get(i).ast(builder, indent, false);
            builder.append('\n');
        }
        if (statements.size() >= 1) {
            statements.get(statements.size() - 1).ast(builder, indent, true);
        }
    }

    @Override
    public String toString() {
        return "statements";
    }
}
