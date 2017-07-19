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

package xyz.avarel.kaiper.runtime;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.Arrays;
import java.util.List;

/**
 * An interface containing all natively implemented operations.
 */
public interface Obj {
    Type<Obj> TYPE = new Type<>("Object");
    Module MODULE = new NativeModule() {{
        declare("TYPE", Obj.TYPE);
    }};

    /**
     * @return The {@link Type} of the object.
     */
    Type getType();

    /**
     * @return The Java object representation of this AJE object or {@code null}.
     */
    default Object toJava() {
        return null;
    }

    /**
     * Addition operator in AJE. Default symbol is {@code +}.
     * <br> Implementation should default to error if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj plus(Obj other) {
        throw unimplemented("plus", other);
    }
    
    /**
     * Subtraction operator in AJE. Default symbol is {@code -}.
     * <br> Implementation should default to error if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj minus(Obj other) {
        throw unimplemented("minus", other);
    }
    
    /**
     * Multiplication operator in AJE. Default symbol is {@code *}.
     * <br> Implementation should default to error if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj times(Obj other) {
        throw unimplemented("multiplication", other);
    }
    
    /**
     * Division operator in AJE. Default symbol is {@code /}.
     * <br> Implementation should default to error if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj divide(Obj other) {
        throw unimplemented("division", other);
    }
    
    /**
     * Modulus operator in AJE. Default symbol is {@code %}.
     * <br> Implementation should default to error if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj mod(Obj other) {
        throw unimplemented("modulus", other);
    }
    
    /**
     * Exponentiation operator in AJE. Default symbol is {@code ^}.
     * <br> Implementation should default to error if not implemented.
     * 
     * @param   other 
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj pow(Obj other) {
        throw unimplemented("exponentiation", other);
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
     * <br> Implementation should default to error if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj greaterThan(Obj other) {
        throw unimplemented("greater than", other);
    }

    /**
     * Less than operator in AJE. Default symbol is {@code <}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj lessThan(Obj other) {
        throw unimplemented("less than", other);
    }

    /**
     * Or operator in AJE. Default symbol is {@code ||}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj or(Obj other) {
        throw unimplemented("or", other);
    }

    /**
     * And operator in AJE. Default symbol is {@code &&}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param   other
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj and(Obj other) {
        throw unimplemented("and", other);
    }

    /**
     * Left Shift operator in AJE. Default symbol is {@code <<}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param other Right {@link Obj} operand.
     * @return The {@link Obj} result of the operation.
     */
    default Obj shl(Obj other) {
        throw unimplemented("shift left", other);
    }

    /**
     * Right Shift operator in AJE. Default symbol is {@code >>}.
     * <br> Implementation should default to check the left-shift of the other object.
     *
     * @param other Right {@link Obj} operand.
     * @return The {@link Obj} result of the operation.
     */
    default Obj shr(Obj other) {
        return other.shl(this);
        //throw unimplemented("shift right", other);
    }

    /**
     * Invcoation operator in AJE. Default symbol is {@code a(b, c...)}.
     * <br> Implementation should default to error if not implemented.
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
     * <br> Implementation should default to error if not implemented.
     *
     * @param   arguments
     *          Array of {@link Obj} arguments.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj invoke(Obj... arguments) {
        return invoke(Arrays.asList(arguments));
    }

    default Obj slice(Obj start, Obj end, Obj step) {
        throw unimplemented("slice");
    }

    /**
     * @return This {@link Obj}.
     */
    default Obj identity() {
        return this;
    }

    /**
     * Get operator in AJE. Default symbol is {@code a[b]}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param   key
     *          Right {@link Obj} operand.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj get(Obj key) {
        throw unimplemented("get");
    }

    default Obj set(Obj key, Obj value) {
        return Undefined.VALUE;
    }

    /**
     * Attribute operator in AJE. Default symbol is {@code a.b}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param   name
     *          Attribute name.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj getAttr(String name) {
        return Undefined.VALUE;
    }

    /**
     * Set attribute operator in AJE. Default symbol is {@code a.b = c}.
     * <br> Implementation should default to error if not implemented.
     *
     * @param   name
     *          Attribute name.
     * @param   value
     *          Value to set attribute to.
     * @return  The {@link Obj} result of the operation.
     */
    default Obj setAttr(String name, Obj value) {
        throw unimplemented("set attribute");
    }

    // no operator, purely internal
    @SuppressWarnings("unchecked")
    default <T extends Obj> T as(Type<T> type) {
        if (getType().is(type)) {
            return (T) this;
        }

        throw new ComputeException("Type mismatch: " + getType() + " can not be casted to " + type);
    }

    default ComputeException unimplemented(String string) {
        return new ComputeException(getType() + " does not support " + string + " operator");
    }

    default ComputeException unimplemented(String string, Obj other) {
        return new ComputeException(getType() + " does not support " + string + " operator with " + other.getType());
    }
}
