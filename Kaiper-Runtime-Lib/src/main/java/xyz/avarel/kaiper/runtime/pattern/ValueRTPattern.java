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

// a: is Int
// a: 2
// a: x
// a: (2, meme: 2, dank: 3)
public class ValueRTPattern extends RTPattern {
    private final Obj obj;

    public ValueRTPattern(Obj obj) {
        super(null);
        this.obj = obj;
    }

    public Obj getObj() {
        return obj;
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return getName() + ": " + getObj();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueRTPattern)) return false;
        ValueRTPattern other = (ValueRTPattern) obj;

        return getObj().equals(other.getObj());
    }
}
