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
import xyz.avarel.aje.runtime.Undefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NativeFunc extends Func {
    //    private final Type receiverType;
    private final List<Parameter> parameters;

    public NativeFunc(String name) {
        this(name, new Parameter[0]);
    }

    public NativeFunc(String name, Parameter... parameters) {
        super(name);
        this.parameters = new ArrayList<>();
        this.parameters.addAll(Arrays.asList(parameters));
    }

    @Override
    public int getArity() {
        return !parameters.isEmpty() && parameters.get(parameters.size() - 1).isRest()
                ? parameters.size() - 1
                : parameters.size();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return super.toString() + "$native";
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (arguments.size() < getArity()) {
            throw new ComputeException(getName() + " requires " + getArity() + " arguments");
        }

        Obj result = eval(arguments);
        return result != null ? result : Undefined.VALUE;
    }

    protected abstract Obj eval(List<Obj> arguments);
}
