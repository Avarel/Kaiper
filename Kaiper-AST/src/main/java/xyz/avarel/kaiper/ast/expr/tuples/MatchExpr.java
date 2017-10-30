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

package xyz.avarel.kaiper.ast.expr.tuples;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.lexer.Position;

import java.util.Map;

public class MatchExpr extends Expr {
    private final Expr target;
    private final Map<PatternCase, Expr> cases;

    public MatchExpr(Position position, Expr target, Map<PatternCase, Expr> cases) {
        super(position);
        this.target = target;
        this.cases = cases;
    }

    public Expr getTarget() {
        return target;
    }

    public Map<PatternCase, Expr> getCases() {
        return cases;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchExpr)) return false;

        MatchExpr matchExpr = (MatchExpr) o;

        return target.equals(matchExpr.target) && cases.equals(matchExpr.cases);
    }

}