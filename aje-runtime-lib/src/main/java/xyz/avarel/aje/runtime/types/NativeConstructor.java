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

package xyz.avarel.aje.runtime.types;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class NativeConstructor extends Constructor {
    private final List<Parameter> parameters;

    public NativeConstructor() {
        this.parameters = Collections.emptyList();
    }

    public NativeConstructor(Parameter... parameters) {
        this.parameters = new ArrayList<>();
        this.parameters.addAll(Arrays.asList(parameters));
    }

    public NativeConstructor(Type... classes) {
        this.parameters = new ArrayList<>();

        for (Type type : classes) {
            this.parameters.add(Parameter.of(type));
        }
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        for (int i = 0; i < getArity(); i++) {
            Parameter parameter = parameters.get(i);

            Type type = parameter.getType();

            if (i < arguments.size()) {
                if (!(arguments.get(i).getType().is(type) || type == Obj.TYPE)) {
                    throw typeError(parameters, arguments);
                }
            } else if (!(parameter.hasDefault() || type == Obj.TYPE)) {
                throw typeError(parameters, arguments);
            }
        }

        Parameter lastParam = parameters.get(parameters.size() - 1);
        if (arguments.size() > getArity() && lastParam.isRest()) {
            Type type = lastParam.getType();
            List<Obj> sublist = arguments.subList(parameters.size() - 1, arguments.size());

            if (type != Obj.TYPE) {
                for (Obj obj : sublist) {
                    if (!obj.getType().is(type)) {
                        throw typeError(parameters, arguments);
                    }
                }
            }
        }

        return eval(arguments);
    }

    protected abstract Obj eval(List<Obj> arguments);
}
