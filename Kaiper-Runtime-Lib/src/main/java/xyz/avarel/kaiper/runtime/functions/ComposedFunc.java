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

package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;

import java.util.List;

public class ComposedFunc extends Func {
    private final Func outer;
    private final Func inner;

    public ComposedFunc(Func outer, Func inner) {
        super(outer.getName() + "<<" + inner.getName());

        this.outer = outer;
        this.inner = inner;

        if (inner.getParameters().size() != 1) {
            throw new ComputeException("Composed functions require the outer function to be arity-1.");
        }
    }

    @Override
    public int getArity() {
        return inner.getArity();
    }

    @Override
    public List<? extends Parameter> getParameters() {
        return inner.getParameters();
    }

    @Override
    public String toString() {
        return super.toString() + "$composed";
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        return outer.invoke(inner.invoke(arguments));
    }
}
