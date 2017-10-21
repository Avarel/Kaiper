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

import xyz.avarel.kaiper.ast.Expr;

// (delegate) = (defaultExpr)
public class DefaultPattern extends NamedPattern {
    private final Pattern delegate;
    private final Expr defaultExpr;

    public DefaultPattern(NamedPattern delegate, Expr defaultExpr) {
        super(delegate.getName());
        this.delegate = delegate;
        this.defaultExpr = defaultExpr;
    }

    public Pattern getDelegate() {
        return delegate;
    }

    public Expr getDefault() {
        return defaultExpr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public boolean optional() {
        return true;
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultExpr;
    }
}
