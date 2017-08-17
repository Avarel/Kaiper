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
import xyz.avarel.kaiper.pattern.PatternCase;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;

public class ComposedFunc extends Func {
    private final Func outer;
    private final Func inner;

    public ComposedFunc(Func outer, Func inner) {
        super(outer.getName() + "<<" + inner.getName());

        this.outer = outer;
        this.inner = inner;

        if (inner.getArity() != 1) {
            throw new ComputeException("Composed functions require the outer function to be arity-1.");
        }
    }

    @Override
    public PatternCase getPattern() {
        return inner.getPattern();
    }

    @Override
    public String toString() {
        return super.toString() + " { " + outer.getName() + "(" + inner.getName() + "(" + getPattern() + ")) }";
    }

    @Override
    public Obj invoke(Tuple argument) {
        return outer.invoke(inner.invoke(argument));
    }
}
