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

package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;

public class ComposedFunction extends Function {
    private final Function outer;
    private final Function inner;

    public ComposedFunction(Function outer, Function inner) {
        super(outer.getName() + "<<" + inner.getName());

        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public String toString() {
        return "def " + outer.getName() + "<<" + inner.getName();
    }

    @Override
    public Obj invoke(Tuple argument) {
        return outer.invoke(new Tuple(inner.invoke(argument)));
    }
}
