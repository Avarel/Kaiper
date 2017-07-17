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

package xyz.avarel.aje.runtime;

import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.modules.Module;
import xyz.avarel.aje.runtime.modules.NativeModule;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.types.NativeConstructor;
import xyz.avarel.aje.runtime.types.Type;

import java.util.List;

public class Str implements Obj {
    public static final Type<Str> TYPE = new Type<>("String", new NativeConstructor(Parameter.of("a")) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj obj = arguments.get(0);
            if (obj instanceof Str) {
                return obj;
            }
            return Str.of(obj.toString());
        }
    });

    public static final Module MODULE = new StrModule();

    private final String value;

    private Str(String value) {
        this.value = value;
    }

    public static Str of(String value) {
        return new Str(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toJava() {
        return value();
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return value();
    }

    public int length() {
        return value().length();
    }

    @Override
    public Obj plus(Obj other) {
        return Str.of(value() + other.toString());
    }

    @Override
    public Obj get(Obj key) {
        if (key instanceof Int) {
            return get((Int) key);
        }
        return Obj.super.get(key);
    }

    private Obj get(Int index) {
        int i = index.value();
        if (i < 0) {
            i += length();
        }
        if (i < 0 || i >= length()) {
            return Undefined.VALUE;
        }
        return Str.of(value().substring(i, i + 1));
    }

    public char get(int i) {
        return value().charAt(i);
    }

    @Override
    public Obj slice(Obj startObj, Obj endObj, Obj stepObj) {
        int start;
        int end;
        int step;

        if (startObj == Undefined.VALUE) {
            start = 0;
        } else {
            if (startObj instanceof Int) {
                start = ((Int) startObj).value();
                if (start < 0) {
                    start += length();
                }
            } else {
                return Undefined.VALUE;
            }
        }

        if (endObj == Undefined.VALUE) {
            end = length();
        } else {
            if (endObj instanceof Int) {
                end = ((Int) endObj).value();
                if (end < 0) {
                    end += length();
                }
            } else {
                return Undefined.VALUE;
            }
        }

        if (stepObj == Undefined.VALUE) {
            step = 1;
        } else {
            if (stepObj instanceof Int) {
                step = ((Int) stepObj).value();
            } else {
                return Undefined.VALUE;
            }
        }

        if (step == 1) {
            return Str.of(value.substring(Math.max(0, start), Math.min(length(), end)));
        } else {
            if (step > 0) {
                StringBuilder buffer = new StringBuilder();

                for (int i = start; i < end; i += step) {
                    buffer.append(get(i));
                }

                return Str.of(buffer.toString());
            } else if (step < 0) {
                StringBuilder buffer = new StringBuilder();

                for (int i = end - 1; i >= start; i += step) {
                    buffer.append(get(i));
                }

                return Str.of(buffer.toString());
            } else { // step == 0
                return Undefined.VALUE;
            }
        }
    }

    public Bool contains(Str str) {
        return Bool.of(value.contains(str.value));
    }

    public Int indexOf(Str str) {
        return Int.of(value.indexOf(str.value));
    }

    public Array split(Str str) {
        Array array = new Array();
        for (String part : value.split(str.value)) {
            array.add(Str.of(part));
        }
        return array;
    }

    public Bool startsWith(Str str) {
        return Bool.of(value.startsWith(str.value));
    }

    public Str substring(Int start) {
        return Str.of(value.substring(start.value()));
    }

    public Str substring(int start) {
        return Str.of(value.substring(start));
    }

    public Str substring(Int start, Int end) {
        return Str.of(value.substring(start.value(), end.value()));
    }

    public Str substring(int start, int end) {
        return Str.of(value.substring(start, end));
    }

    public Array toVector() {
        Array array = new Array();
        for (int i = 0; i < length(); i++) {
            array.add(substring(i, i + 1));
        }
        return array;
    }

    public Str toLowerCase() {
        return Str.of(value.toLowerCase());
    }

    public Str toUpperCase() {
        return Str.of(value.toUpperCase());
    }

    public Str trim() {
        return Str.of(value.trim());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Str && value().equals(((Str) obj).value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "size":
                return Int.of(length());
            case "length":
                return Int.of(length());
            case "lastIndex":
                return Int.of(length() - 1);
            default:
                return Obj.super.getAttr(name);
        }
    }

    private static class StrModule extends NativeModule {
        public StrModule() {
            declare("length", new NativeFunc("length", Parameter.of("string")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of(arguments.get(0).castTo(Str.TYPE).length());
                }
            });

            declare("contains", new NativeFunc("contains", Parameter.of("string"), Parameter.of("query")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).contains((Str) arguments.get(1));
                }
            });
            declare("indexOf", new NativeFunc("indexOf", Parameter.of("string"), Parameter.of("query")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).indexOf((Str) arguments.get(1));
                }
            });
            declare("split", new NativeFunc("split", Parameter.of("string"), Parameter.of("query")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).split((Str) arguments.get(1));
                }
            });
            declare("substring", new NativeFunc("substring", Parameter.of("string"), Parameter.of("index")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    if (arguments.size() >= 3) {
                        if (arguments.get(1) instanceof Int) {
                            return arguments.get(0).castTo(Str.TYPE).substring((Int) arguments.get(1), (Int) arguments.get(2));
                        }
                        return Undefined.VALUE;
                    } else {
                        return arguments.get(0).castTo(Str.TYPE).substring((Int) arguments.get(1));
                    }
                }
            });
            declare("toVector", new NativeFunc("toVector", Parameter.of("string")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).toVector();
                }
            });
            declare("toLowerCase", new NativeFunc("toLowerCase", Parameter.of("string")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).toLowerCase();
                }
            });
            declare("toUpperCase", new NativeFunc("toUpperCase", Parameter.of("string")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).toUpperCase();
                }
            });
            declare("trim", new NativeFunc("trim", Parameter.of("string")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).castTo(Str.TYPE).trim();
                }
            });
        }
    }
}
