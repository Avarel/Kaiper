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

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class NativeFunction extends AJEFunction {
    private final List<Parameter> parameters;
    private final boolean varargs;

    public NativeFunction(Type... types) {
        this.parameters = Arrays.stream(types).map(Parameter::new).collect(Collectors.toList());
        this.varargs = false;
    }

    public NativeFunction(boolean varargs, Type type) {
        this.parameters = Collections.singletonList(new Parameter(type));
        this.varargs = varargs;
    }

    protected abstract Obj eval(List<Obj> arguments);

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    @Override
    public Obj invoke(List<Obj> args) {
        if (!varargs && args.size() != getArity()) {
            return Undefined.VALUE;
        }

        if (!varargs) {
            for (int i = 0; i < parameters.size(); i++) {
                if (!args.get(i).getType().is((Type) parameters.get(i).getType().compute())) {
                    return Undefined.VALUE;
                }
            }
        } else {
            for (Obj argument : args) { // all varargs should be the same size
                if (!argument.getType().is((Type) parameters.get(0).getType().compute())) {
                    return Undefined.VALUE;
                }
            }
        }

        Obj result = eval(args);
        return result != null ? result : Undefined.VALUE;
    }

    @Override
    public String toString() {
        return "native function(" + parameters.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
