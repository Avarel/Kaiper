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

package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.value.NullNode;

// a: is Int
// a: 2
// a: x
// a: (2, meme: 2, dank: 3)
public class ValuePattern extends Pattern {
    private final Expr expr;

    public ValuePattern(Expr expr) {
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public int compareTo(Pattern other) {
        int compare = super.compareTo(other);

        if (compare == 0 && other instanceof ValuePattern) {
            ValuePattern value = (ValuePattern) other;

            // special workaround for null value atterns
            if (expr == NullNode.VALUE) {
                return expr == value.expr ? 0 : 1;
            } else if (value.expr == NullNode.VALUE) {
                return expr == value.expr ? 0 : -1;
            }

            return Integer.compare(expr.hashCode(), value.expr.hashCode());
        }

        return compare;
    }

    @Override
    public String toString() {
        return getExpr().toString();
    }

    @Override
    public int nodeWeight() {
        return 1;
    }
}