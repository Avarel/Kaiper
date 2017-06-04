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

package xyz.avarel.aje.runtime.classes;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Text;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.NativeFunction;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.scope.Scope;

import java.util.List;

public class TextType extends Type<Text> {
    private Scope scope = new Scope();
    
    public TextType() {
        super("string");

        scope.declare("contains", new NativeFunction(this, Text.TYPE) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).contains((Text) arguments.get(0));
            }
        });
        scope.declare("indexOf", new NativeFunction(this, Text.TYPE) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).indexOf((Text) arguments.get(0));
            }
        });
        scope.declare("split", new NativeFunction(this, Text.TYPE) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).split((Text) arguments.get(0));
            }
        });
        scope.declare("substring", new NativeFunction(this, Int.TYPE) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                if (arguments.size() >= 2) {
                    if (arguments.get(1) instanceof Int) {
                        return ((Text) receiver).substring((Int) arguments.get(0), (Int) arguments.get(1));
                    }
                    return Undefined.VALUE;
                } else {
                    return ((Text) receiver).substring((Int) arguments.get(0));
                }
            }
        });
        scope.declare("toVector", new NativeFunction(this) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).toVector();
            }
        });
        scope.declare("toLowerCase", new NativeFunction(this) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).toLowerCase();
            }
        });
        scope.declare("toUpperCase", new NativeFunction(this) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).toUpperCase();
            }
        });
        scope.declare("trim", new NativeFunction(this) {
            @Override
            protected Obj eval(Obj receiver, List<Obj> arguments) {
                return ((Text) receiver).trim();
            }
        });
    }

    @Override
    public Obj getAttr(String name) {
        if (scope.contains(name)) {
            return scope.lookup(name);
        }

        return super.getAttr(name);
    }
}
