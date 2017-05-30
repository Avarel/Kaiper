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

import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class ComposedFunction extends AJEFunction {
    private final AJEFunction left;
    private final AJEFunction right;

    public ComposedFunction(AJEFunction left, AJEFunction right) {
        this.left = left;
        this.right = right;

        if (left.getParameters().size() != 1 || right.getParameters().size() != 1) {
            throw new ComputeException("Composed functions require both functions to be arity-1.");
        }
    }

    @Override
    public List<Parameter> getParameters() {
        return left.getParameters();
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        return "compose(" + left + ", " + right + ")";
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (arguments.size() != getArity()) {
            return Undefined.VALUE;
        }

        return left.invoke(right.invoke(arguments));
    }
}
