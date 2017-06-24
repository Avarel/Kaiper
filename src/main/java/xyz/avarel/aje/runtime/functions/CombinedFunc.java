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

import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CombinedFunc extends Func {
    private final Func left;
    private final Func right;
    private final BinaryOperator<Obj> operator;

    public CombinedFunc(Func left, Func right, BinaryOperator<Obj> operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;

        if (left.getParameters().size() != right.getParameters().size()) {
            throw new ComputeException("Combined functions require both functions to have the same arity.");
        } else {
            for (int i = 0; i < left.getParameters().size(); i++) {
                if (left.getParameters().get(i).getCls() != right.getParameters().get(i).getCls()) {
                    throw new ComputeException("Combined functions require both functions to have the same parameters.");
                }
            }
        }
    }

    @Override
    public int getArity() {
        return left.getArity();
    }

    @Override
    public List<Parameter> getParameters() {
        return left.getParameters();
    }

    @Override
    public String toString() {
        return "combined$" + super.toString();
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (arguments.size() != getArity()) {
            return Undefined.VALUE;
        }

        return operator.apply(left.invoke(arguments), right.invoke(arguments));
    }
}
