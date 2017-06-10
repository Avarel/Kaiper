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

import xyz.avarel.aje.runtime.Cls;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class NativeFunc extends Func {
    //    private final Type receiverType;
    private final List<Parameter> parameters;
    private final boolean varargs;

    public NativeFunc() {
        this.parameters = Collections.emptyList();
        this.varargs = false;
    }

    public NativeFunc(Parameter... parameters) {
        this.parameters = new ArrayList<>();
        this.parameters.addAll(Arrays.asList(parameters));
        this.varargs = false;
    }

    public NativeFunc(Cls... classes) {
        this.parameters = new ArrayList<>();

        for (Cls cls : classes) {
            this.parameters.add(Parameter.of(cls));
        }

        this.varargs = false;
    }

    public NativeFunc(boolean varargs, Cls parameter) { // a : Int...
        //this.parameters = new ArrayList<>();

        this.parameters = Collections.singletonList(Parameter.of(parameter));
        this.varargs = varargs;
    }

    protected abstract Obj eval(List<Obj> arguments);

    @Override
    public int getArity() {
        return parameters.size();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "native$" + super.toString();
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (!varargs && arguments.size() < getArity()) {
            return Undefined.VALUE;
        }

        if (!varargs) {
            for (int i = 0; i < parameters.size(); i++) {
                if (!arguments.get(i).getType().is(parameters.get(i).getCls())) {
                    return Undefined.VALUE;
                }
            }
        } else {
            for (Obj argument : arguments) { // all varargs should be the same size
                if (!argument.getType().is(parameters.get(0).getCls())) {
                    return Undefined.VALUE;
                }
            }
        }

        Obj result = eval(arguments);
        return result != null ? result : Undefined.VALUE;
    }
}
