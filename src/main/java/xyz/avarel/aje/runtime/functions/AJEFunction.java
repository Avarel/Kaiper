/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.NativeObject;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.List;
import java.util.function.Function;

public abstract class AJEFunction implements Obj, NativeObject<Function<List<Obj>, Obj>> {
    public static final Type<AJEFunction> TYPE = new Type<>("function");

    public abstract int getArity();

    @Override
    public Type<AJEFunction> getType() {
        return TYPE;
    }

    @Override
    public Function<List<Obj>, Obj> toNative() {
        return this::invoke;
    }

    @Override
    public abstract Obj invoke(List<Obj> args);

    @Override
    public Obj plus(Obj other) {
        if (other instanceof AJEFunction) {
            return plus((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction plus(AJEFunction right) {
        return new CombinedFunction(this, right, Obj::plus);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof AJEFunction) {
            return minus((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction minus(AJEFunction right) {
        return new CombinedFunction(this, right, Obj::minus);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof AJEFunction) {
            return times((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction times(AJEFunction right) {
        return new CombinedFunction(this, right, Obj::times);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof AJEFunction) {
            return divide((AJEFunction) other);
        }
        return Undefined.VALUE;
    }

    private AJEFunction divide(AJEFunction right) {
        return new CombinedFunction(this, right, Obj::divide);
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "arity":
                return Int.of(getArity());
        }
        return Undefined.VALUE;
    }
}
