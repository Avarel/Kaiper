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

package xyz.avarel.aje.runtime.lists;

import xyz.avarel.aje.runtime.*;
import xyz.avarel.aje.runtime.functions.NativeFunction;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * AJE wrapper class for a one dimensional vector.
 */
public class Vector extends ArrayList<Obj> implements Obj, Iterable<Obj>, NativeObject<List<Object>> {
    public static final Type TYPE = new Type("vector");

    /**
     * Creates an empty vector.
     */
    public Vector() {
        super();
    }

    /**
     * Creates a vector of items.
     *
     * @param   items
     *          {@link Obj} items to put into the list.
     * @return  The created {@link Vector}.
     */
    public static Vector of(Obj... items) {
        Vector vector = new Vector();
        vector.addAll(Arrays.asList(items));
        return vector;
    }

    /**
     * Creates a vector of items from a native {@link Collection collection}.
     *
     * @param   collection
     *          Native {@link Collection collection} of {@link Obj AJE objects}.
     * @return  The created {@link Vector}.
     */
    public static Vector ofList(Collection<Obj> collection) {
        Vector vector = new Vector();
        vector.addAll(collection);
        return vector;
    }

    /**
     * Returns an unmodifiable representation of the vector. Note that the list's contents are all converted to
     * their native representation or {@code null} if unable to.
     *
     * @return An unmodifiable representation of the vector.
     */
    @Override
    public List<Object> toNative() {
        List<Object> objects = new ArrayList<>();

        for (Obj obj : this) {
            objects.add(obj.isNativeObject() ? obj.toNative() : null);
        }

        return Collections.unmodifiableList(objects);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj plus(Obj other) {
        if (other instanceof Vector) {
            return plus((Vector) other);
        }
        return plus(Vector.of(other));
    }

    private Vector plus(Vector other) {
        return listOperation(other, Obj::plus);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Vector) {
            return minus((Vector) other);
        }
        return minus(Vector.of(other));
    }

    private Vector minus(Vector other) {
        return listOperation(other, Obj::minus);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Vector) {
            return times((Vector) other);
        }
        return times(Vector.of(other));
    }

    private Vector times(Vector other) {
        return listOperation(other, Obj::times);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Vector) {
            return divide((Vector) other);
        }
        return divide(Vector.of(other));
    }

    private Vector divide(Vector other) {
        return listOperation(other, Obj::divide);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Vector) {
            return pow((Vector) other);
        }
        return pow(Vector.of(other));
    }

    private Vector pow(Vector other) {
        return listOperation(other, Obj::pow);
    }

    @Override
    public Vector negative() {
        return listOperation(Obj::negative);
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Vector) {
            return isEqualTo((Vector) other);
        }
        return Bool.FALSE;
    }

    public Bool isEqualTo(Vector other) {
        if (this == other) {
            return Bool.TRUE;
        } else if (size() != other.size()) {
            return Bool.FALSE;
        }

        Vector vector = listOperation(other, Obj::isEqualTo);
        for (Obj o : vector) {
            if (!(o instanceof Bool)) {
                if (o == Bool.FALSE) {
                    return Bool.FALSE;
                }
            }
        }

        return Bool.TRUE;
    }

    private Vector listOperation(Vector other, BinaryOperator<Obj> operator) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Vector vector = Vector.of();
        for (int i = 0; i < len; i++) {
            vector.add(Numeric.process(get(i % size()), other.get(i % other.size()), operator));
        }
        return vector;
    }

    private Vector listOperation(UnaryOperator<Obj> operator) {
        Vector vector = Vector.of();
        for (int i = 0; i < size(); i++) {
            vector.add(operator.apply(get(i % size())));
        }
        return vector;
    }

    @Override
    public Obj get(Obj other) {
        if (other instanceof Int) {
            return get((Int) other);
        }
        return Undefined.VALUE;
    }

    private Obj get(Int index) {
        int i = index.value();
        if (i < 0) {
            i += size();
        }
        if (i < 0 || i >= size()) {
            return Undefined.VALUE;
        }
        return this.get(i);
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "size":
                return Int.of(size());
            case "length":
                return Int.of(size());
            case "lastIndex":
                return Int.of(size() - 1);
            case "append":
                return new NativeFunction(true, Obj.TYPE) {
                    @Override
                    protected Obj eval(List<Obj> arguments) {
                        Vector.this.addAll(arguments);
                        return Vector.this;
                    }
                };
            case "extend":
                return new NativeFunction(Vector.TYPE) {
                    @Override
                    protected Obj eval(List<Obj> arguments) {
                        Vector.this.addAll(arguments);
                        return Vector.this;
                    }
                };
        }
        return Undefined.VALUE;
    }
}
