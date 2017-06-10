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

import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.functions.ReferenceFunc;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.Arrays;
import java.util.List;

/**
 * An interface containing all natively implemented operations.
 */
public interface Obj<JAVA> {
    Cls<Obj> CLS = new ObjCls();

    /**
     * @return The {@link Cls} of the object.
     */
    Cls getType();

    /**
     * @return The {@link JAVA java} object representation of this AJE object or {@code null}.
     */
    default JAVA toJava() {
        return null;
    }

    /**
     * Addition operator in AJE. Default symbol is {@code +}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj plus(Obj other) {
        return Undefined.VALUE;
    }
    
    /**
     * Subtraction operator in AJE. Default symbol is {@code -}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj minus(Obj other) {
        return Undefined.VALUE;
    }
    
    /**
     * Multiplication operator in AJE. Default symbol is {@code *}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj times(Obj other) {
        return Undefined.VALUE;
    }
    
    /**
     * Division operator in AJE. Default symbol is {@code /}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj divide(Obj other) {
        return Undefined.VALUE;
    }
    
    /**
     * Modulus operator in AJE. Default symbol is {@code %}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj mod(Obj other) {
        return Undefined.VALUE;
    }
    
    /**
     * Exponentiation operator in AJE. Default symbol is {@code ^}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj pow(Obj other) {
        return Undefined.VALUE;
    }
    
    /**
     * Negative numeric unary operator in AJE. Default symbol is {@code -}.
     *
     * @return  The {@link Obj} result of the operation.
     */
    default Obj negative() {
        return Undefined.VALUE;
    }
    
    /**
     * Negation operator in AJE. Default symbol is {@code !}.
     *
     * @return  The {@link Obj} result of the operation.
     */
    default Obj negate() {
        return Undefined.VALUE;
    }

    /**
     * Equality operator in AJE. Default symbol is {@code ==}.
     * <br> Implementation should defaults to returning {@link Bool} representation of {@link Object#equals(Object)}
     * if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj isEqualTo(Obj other) {
        return Bool.of(this.equals(other));
    }

    /**
     * Greater than operator in AJE. Default symbol is {@code >}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj greaterThan(Obj other) {
        return Undefined.VALUE;
    }

    /**
     * Less than operator in AJE. Default symbol is {@code <}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj lessThan(Obj other) {
        return Undefined.VALUE;
    }

    /**
     * Or operator in AJE. Default symbol is {@code ||}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj or(Obj other) {
        return Undefined.VALUE;
    }

    /**
     * And operator in AJE. Default symbol is {@code &&}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj and(Obj other) {
        return Undefined.VALUE;
    }

    /**
     * Invcoation operator in AJE. Default symbol is {@code a(b, c...)}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   arguments
     *          List of {@link Obj} arguments.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj invoke(List<Obj> arguments) {
        return Undefined.VALUE;
    }

    /**
     * Invcoation operator in AJE. Default symbol is {@code a(b, c...)}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   arguments
     *          Array of {@link Obj} arguments.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj invoke(Obj... arguments) {
        return invoke(Arrays.asList(arguments));
    }

    default Obj slice(Obj start, Obj end, Obj step) {
        return Undefined.VALUE;
    }

    /**
     * @return  This {@link Obj}.
     */
    default Obj identity() {
        return this;
    }

    default Obj set(Obj key, Obj value) {
        return Undefined.VALUE;
    }

    /**
     * Get operator in AJE. Default symbol is {@code a[b]}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   key
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj get(Obj key) {
        return Undefined.VALUE;
    }

    /**
     * Attribute operator in AJE. Default symbol is {@code a.b}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   name
     *          Attribute name.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj getAttr(String name) {
        Obj obj = getType().getAttr(name);

        if (obj instanceof Func) {
            return new ReferenceFunc(this, (Func) obj);
        }

        return Undefined.VALUE;
    }

    /**
     * Set attribute operator in AJE. Default symbol is {@code a.b = c}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   name
     *          Attribute name.
     * @param   value
     *          Value to set attribute to.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj setAttr(String name, Obj value) {
        return Undefined.VALUE;
    }






    default Obj plus(int other) {
        return plus(Int.of(other));
    }
    default Obj minus(int other) {
        return minus(Int.of(other));
    }
    default Obj times(int other) {
        return times(Int.of(other));
    }
    default Obj divide(int other) {
        return divide(Int.of(other));
    }
    default Obj mod(int other) {
        return mod(Int.of(other));
    }
    default Obj pow(int other) {
        return pow(Int.of(other));
    }

    default Obj plus(double other) {
        return plus(Decimal.of(other));
    }
    default Obj minus(double other) {
        return minus(Decimal.of(other));
    }
    default Obj times(double other) {
        return times(Decimal.of(other));
    }
    default Obj divide(double other) {
        return divide(Decimal.of(other));
    }
    default Obj mod(double other) {
        return mod(Decimal.of(other));
    }
    default Obj pow(double other) {
        return pow(Decimal.of(other));
    }

    class ObjCls extends Cls<Obj> {
        public ObjCls() {
            super("Object");

            getScope().declare("toString", new NativeFunc(Parameter.of("self")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Str.of(arguments.get(0).toString());
                }
            });

//            getScope().declare("plus", new NativeFunction(this, this) {
//                @Override
//                protected Obj eval(List<Obj> arguments) {
//                    return receiver.plus(arguments.get(0));
//                }
//            });

            getScope().declare("get", new NativeFunc(Parameter.of("self"), Parameter.of(this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).get(arguments.get(1));
                }
            });
            getScope().declare("set", new NativeFunc(Parameter.of("self"), Parameter.of(this), Parameter.of(this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).set(arguments.get(1), arguments.get(2));
                }
            });
        }
    }
}
