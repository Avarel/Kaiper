/*
 * Licensed under the Apache License, Version 2.0 (the
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

import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Func implements Obj<Function<List<Obj>, Obj>> {
    public static final Type<Func> TYPE = new FunctionType();

    protected static ComputeException typeError(List<Parameter> parameters, List<Obj> arguments) {
        StringJoiner argType = new StringJoiner(", ", "(", ")");
        for (Obj obj : arguments) argType.add(obj.getType().getName());

        StringJoiner funcType = new StringJoiner(", ", "(", ")");
        for (Parameter param : parameters) funcType.add(param.typeString());

        return new ComputeException(argType + " can not apply to " + funcType);
    }

    public abstract int getArity();

    public abstract List<Parameter> getParameters();

    @Override
    public Type<Func> getType() {
        return TYPE;
    }

    @Override
    public Function<List<Obj>, Obj> toJava() {
        return this::invoke;
    }

    @Override
    public abstract Obj invoke(List<Obj> arguments);

    @Override
    public String toString() {
        return "func(" + getParameters().stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }

    public static class FunctionType extends Type<Func> {
        public FunctionType() {
            super("Function");

            getScope().declare("compose", new NativeFunc(Parameter.of("self", this), Parameter.of(this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return new ComposedFunc((Func) arguments.get(0), (Func) arguments.get(1));
                }
            });
        }
    }
}
