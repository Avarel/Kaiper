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

import java.util.ArrayList;
import java.util.List;

public class ReferenceFunc extends Func {
    private final Obj receiver;
    private final Func func;

    public ReferenceFunc(Obj receiver, Func func) {
        this.receiver = receiver;
        this.func = func;

        if (func.getParameters().isEmpty()
                || func.getParameters().get(0).getName() == null
                || !func.getParameters().get(0).getName().equals("self")) {
            throw new ComputeException("Function does not take receivers.");
        } // TODO stuff
    }

    @Override
    public int getArity() {
        return func.getArity() - 1;
    }

    @Override
    public List<Parameter> getParameters() {
        return func.getParameters().subList(1, func.getParameters().size());
    }

    @Override
    public String toString() {
        return receiver + "." + super.toString();
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        List<Obj> args = new ArrayList<>();
        args.add(receiver);
        args.addAll(arguments);

        return func.invoke(args);
    }
}
