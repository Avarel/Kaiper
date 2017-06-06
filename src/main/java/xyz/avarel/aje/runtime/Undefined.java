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

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public enum Undefined implements Obj<Undefined> {
    VALUE;

    public static final Type<Undefined> TYPE = new Type<>("undefined");

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public Undefined toJava() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
