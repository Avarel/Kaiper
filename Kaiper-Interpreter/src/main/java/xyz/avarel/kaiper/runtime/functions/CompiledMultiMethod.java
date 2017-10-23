/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.ast.pattern.PatternBinder;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.scope.Scope;

import java.util.SortedSet;
import java.util.TreeSet;

public class CompiledMultiMethod extends Func {
    private final SortedSet<CompiledFunc> branches;

    public CompiledMultiMethod(String name) {
        this(name, new TreeSet<>());
    }

    public CompiledMultiMethod(String name, SortedSet<CompiledFunc> branches) {
        super(name);
        this.branches = branches;
    }

    public SortedSet<CompiledFunc> getBranches() {
        return branches;
    }

    public boolean addCase(CompiledFunc func) {
        return branches.add(func);
    }

    @Override
    public int getArity() {
        return branches.first().getPattern().size();
    }

    @Override
    public Obj divide(Obj other) {
       if (other instanceof Int) {
           return specificArity(((Int) other));
       }
       return super.divide(other);
    }

    public Func specificArity(Int other) {
        SortedSet<CompiledFunc> b = new TreeSet<>();
        for (CompiledFunc func : branches) {
            if (func.getArity() == other.value()) {
                b.add(func);
            }
        }
        return b.size() == 1 ? b.first() : new CompiledMultiMethod(getName(), b);
    }

    @Override
    public Obj invoke(Tuple argument) {
        for (CompiledFunc entry : branches) {
            if (argument.size() < entry.getPattern().arity()) {
                continue;
            }

            Scope<String, Obj> scope = entry.getScope().subScope();

            if (!new PatternBinder(entry.getVisitor(), scope).bind(entry.getPattern(), argument)) {
                continue;
            }

            try {
                return entry.getExpr().accept(entry.getVisitor(), scope);
            } catch (ReturnException re) {
                return re.getValue();
            }
        }

        throw new InterpreterException("Could not match arguments " + argument + " to any method cases");
    }

    @Override
    public String toString() {
        return super.toString() + (
                branches.size() == 1
                        ? branches.first().getPattern()
                        : "(" + branches.size() + " definitions)"
        );
    }
}
