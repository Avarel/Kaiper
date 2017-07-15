///*
// * Licensed under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//package xyz.avarel.aje.oldbytecode;
//
//import xyz.avarel.aje.exceptions.InvalidBytecodeException;
//
///**
// * Custom bytecode instructions.
// *
// * @author AdrianTodt
// */
//public enum Bytecode {
//    //Special Instruction (Always need to be 0)
//    END,
//
//    //Expressions
//    RETURN, DECLARE, ASSIGN, CONDITIONAL, FOREACH,
//
//    //Nodes
//    ARRAY, DICTIONARY, RANGE,
//
//    //Value Nodes
//    UNDEFINED, BOOLEAN, INT, DECIMAL, STRING,
//
//    //Function Nodes (Function Param is a Reserved instruction for Data)
//    FUNCTION, FUNCTION_PARAM,
//
//    //Identifier and Invocation
//    IDENTIFIER, INVOCATION,
//
//    //Operations
//    UNARY_OP, BINARY_OP, SLICE_OP, GET, SET;
//
//    public static Bytecode byID(byte id) {
//        Bytecode[] values = values();
//        if (id < values.length) return values[id];
//        throw new InvalidBytecodeException("Invalid Instruction");
//    }
//
//    public byte id() {
//        return (byte) ordinal();
//    }
//}
