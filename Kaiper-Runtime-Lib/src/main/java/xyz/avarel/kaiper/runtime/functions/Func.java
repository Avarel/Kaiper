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
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.function.Function;

public abstract class Func implements Obj {
    public static final Type<Func> TYPE = new Type<>("Function");
    public static final Module MODULE = new NativeModule("Function") {{
        declare("TYPE", Func.TYPE);
    }};
    private final String name;

    protected Func(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? "anonymous" : name;
    }

    @Override
    public Type<Func> getType() {
        return TYPE;
    }

    @Override
    public Function<Tuple, Obj> toJava() {
        return this::invoke;
    }

    @Override
    public abstract Obj invoke(Obj argument);

    @Override
    public Obj shr(Obj other) { // this >> other
        if (other instanceof Func) {
            return new ComposedFunc((Func) other, this);
        }
        return Obj.super.shr(other);
    }

    @Override
    public Obj shl(Obj other) { // this << other
        if (other instanceof Func) {
            return new ComposedFunc(this, (Func) other);
        }
        return Obj.super.shr(other);
    }

    @Override
    public String toString() {
        return "def " + getName();
    }
}
