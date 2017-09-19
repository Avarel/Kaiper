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

package xyz.avarel.kaiper.vm.runtime.types;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.types.Type;
import xyz.avarel.kaiper.scope.Scope;

// fuck inheritance
public class CompiledObj implements Obj {
    private final CompiledType type;
    private final Scope scope;

    @SuppressWarnings("unchecked")
    public CompiledObj(CompiledType type, Scope scope) {
        this.type = type;
        this.scope = scope;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Obj getAttr(String name) {
        Obj obj = scope.getMap().get(name);

        return obj == null ? Obj.super.getAttr(name) : obj;
    }

//    @Override
//    public boolean hasAttr(String name) {
//        return scope.contains(name);
//    }

    @Override
    public Obj setAttr(String name, Obj value) {
        scope.declare(name, value);
        return Null.VALUE;
    }

    @Override
    public String toString() {
        Obj method = scope.get("toString");
        if (method == null) {
            return type.toString() + "$" + hashCode();
        } else {
            return method.invoke(this).toString();
        }
    }

    public Scope getScope() {
        return scope;
    }
}
