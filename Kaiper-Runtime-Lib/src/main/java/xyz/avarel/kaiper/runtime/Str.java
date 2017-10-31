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

package xyz.avarel.kaiper.runtime;

import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.types.Type;

public class Str implements Obj {
    public static final Type<Str> TYPE = new Type<>("String");
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
            return getChar((Int) key);
        }
        return Obj.super.get(key);
    }

    private Obj getChar(Int index) {
        int i = index.value();
        if (i < 0) {
            i += length();
        }
        if (i < 0 || i >= length()) {
            return Null.VALUE;
        }
        return Str.of(value().substring(i, i + 1));
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
}
