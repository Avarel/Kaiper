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

package xyz.avarel.kaiper.runtime.pattern;

import xyz.avarel.kaiper.runtime.Obj;

// (delegate) = (defaultExpr)
public class DefaultRuntimePattern extends RuntimePattern {
    private final RuntimePattern delegate;
    private final Obj defaultObj;

    public DefaultRuntimePattern(RuntimePattern delegate, Obj defaultObj) {
        super(delegate.getName());
        this.delegate = delegate;
        this.defaultObj = defaultObj;
    }

    public RuntimePattern getDelegate() {
        return delegate;
    }

    public Obj getDefault() {
        return defaultObj;
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultObj;
    }
}
