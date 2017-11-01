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
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.List;

public abstract class Function implements Obj {
    public static final Type<Function> TYPE = new Type<>("Function");
    private final String name;

    protected Function(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? "anonymous" : name;
    }

    @Override
    public Type<Function> getType() {
        return TYPE;
    }

    @Override
    public abstract Obj invoke(List<Obj> arguments);

    @Override
    public Obj shr(Obj other) { // this >> other
        if (other instanceof Function) {
            return new ComposedFunction((Function) other, this);
        }
        return Obj.super.shr(other);
    }

    @Override
    public Obj shl(Obj other) { // this << other
        if (other instanceof Function) {
            return new ComposedFunction(this, (Function) other);
        }
        return Obj.super.shr(other);
    }

    @Override
    public String toString() {
        return "def " + getName();
    }
}
