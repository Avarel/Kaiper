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

public enum Bool implements Obj, NativeObject<Boolean> {
    TRUE(true),
    FALSE(false);

    public static final Type TYPE = new Type("boolean");

    private final boolean value;

    Bool(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Boolean toNative() {
        return value;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj or(Obj other) {
        if (other instanceof Bool) {
            return or((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool or(Bool other) {
        if (value) return TRUE;
        if (other.value) return TRUE;
        return FALSE;
    }

    @Override
    public Obj and(Obj other) {
        if (other instanceof Bool) {
            return and((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool and(Bool other) {
        if (!value) return FALSE;
        if (!other.value) return FALSE;
        return TRUE;
    }

    @Override
    public Bool negate() {
        return value ? FALSE : TRUE;
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Bool) {
            return value == ((Bool) other).value ? TRUE : FALSE;
        }
        return FALSE;
    }
}
