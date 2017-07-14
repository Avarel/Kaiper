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

import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.functions.ReferenceFunc;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.types.Type;

import java.util.Arrays;
import java.util.List;

/**
 * An interface containing all natively implemented operations.
 *
 * @param <J> Java representation of the object.
 */
public interface Obj<J> {
    Type<Obj> TYPE = new ObjType();

    /**
     * @return The {@link Type} of the object.
     */
    Type getType();

    /**
     * @return The {@link J java} object representation of this AJE object or {@code null}.
     */
    default J toJava() {
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
        throw unimplemented("plus");
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
        throw unimplemented("minus");
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
        throw unimplemented("multiplication");
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
        throw unimplemented("division");
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
        throw unimplemented("modulus");
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
        throw unimplemented("exponentiation");
    }
    
    /**
     * Negative numeric unary operator in AJE. Default symbol is {@code -}.
     *
     * @return  The {@link Obj} result of the operation.
     */
    default Obj negative() {
        throw unimplemented("numeric negation");
    }

    /**
     * Negation operator in AJE. Default symbol is {@code !}.
     *
     * @return  The {@link Obj} result of the operation.
     */
    default Obj negate() {
        throw unimplemented("boolean negation");
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
        throw unimplemented("greater than");
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
        throw unimplemented("less than");
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
        throw unimplemented("or");
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
        throw unimplemented("and");
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
        throw unimplemented("invocation");
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

    /**
     * Get operator in AJE. Default symbol is {@code a[b]}.
     * <br> Implementation should defaults to returning {@link Undefined#VALUE} if not implemented.
     *
     * @param   key
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj get(Obj key) {
        throw unimplemented("get");
    }

    default Obj set(Obj key, Obj value) {
        throw unimplemented("set");
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

        throw new ComputeException("Unable to read attribute " + name + " of " + toString());
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

    default ComputeException unimplemented(String string) {
        return new ComputeException(getType() + " does not support " + string + " operator");
    }

    class ObjType extends Type<Obj> {
        public ObjType() {
            super("Object");
        }

        public void initialize() {
            getScope().declare("toString", new NativeFunc(Parameter.of("self", this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Str.of(arguments.get(0).toString());
                }
            });

            getScope().declare("getType", new NativeFunc(Parameter.of("self", this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).getType();
                }
            });

            getScope().declare("get", new NativeFunc(Parameter.of("self", this), Parameter.of(this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).get(arguments.get(1));
                }
            });
            getScope().declare("set",
                    new NativeFunc(Parameter.of("self", this), Parameter.of(this), Parameter.of(this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0).set(arguments.get(1), arguments.get(2));
                }
            });
        }
    }
}
