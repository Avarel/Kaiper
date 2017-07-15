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
//package xyz.avarel.aje.oldbytecode.viewer;
//
//import xyz.avarel.aje.ast.operations.BinaryOperatorType;
//import xyz.avarel.aje.ast.operations.UnaryOperatorType;
//import xyz.avarel.aje.compiler.AJEBytecode;
//import xyz.avarel.aje.oldbytecode.Bytecode;
//import xyz.avarel.aje.exceptions.InvalidBytecodeException;
//
//import java.io.DataInput;
//import java.io.IOException;
//import java.util.Arrays;
//
///**
// * @author AdrianTodt
// */
//public class ExprViewer {
//    public static String view(DataInput input) throws IOException {
//        byte[] identifier = {input.readByte(), input.readByte(), input.readByte()};
//        if (!Arrays.equals(identifier, AJEBytecode.IDENTIFIER)) {
//            throw new InvalidBytecodeException("Invalid Itentifier");
//        }
//
//        int versionMajor = input.readInt(), versionMinor = input.readInt();
//
//        StringBuilder builder = new StringBuilder("Header: AJE").append(versionMajor).append('.').append(versionMinor).append('\n');
//
//        if (versionMajor != AJEBytecode.BYTECODE_VERSION_MAJOR || versionMinor > AJEBytecode.BYTECODE_VERSION_MINOR) {
//            builder.append("(Bytecode viewing might be incorrect)\n");
//        }
//
//        builder.append('\n');
//
//        collectUntil(input, builder, "", -1);
//
//        return builder.toString();
//    }
//
//    private static void collectUntil(DataInput input, StringBuilder output, String tab, int validEndId) throws IOException {
//        while (nextExpr(input, output, tab, validEndId));
//    }
//
//    private static boolean nextExpr(DataInput input, StringBuilder output, String tab, int validEndId) throws IOException {
//        byte bytecode = input.readByte();
//        switch (Bytecode.byID(bytecode)) {
//            case END: {
//                /*
//                END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("END endId=").append(endId);
//                if (endId != validEndId) output.append(" (Invalid)");
//                output.append('\n');
//                return false;
//            }
//            case RETURN: {
//                /*
//                RETURN endId (... => expr) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("RETURN endId=").append(endId).append('\n');
//                output.append(tab).append(">expr\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case ASSIGN: {
//                /*
//                ASSIGN declaration name hasParent endId [hasParent ? (... => parent) END endId] (... => expr) END endId
//                 */
//                boolean declaration = input.readBoolean();
//                String name = input.readUTF();
//                boolean hasParent = input.readBoolean();
//                int endId = input.readInt();
//
//                output.append(tab).append("ASSIGN declaration=").append(declaration)
//                        .append(" name=").append(name)
//                        .append(" endId=").append(endId).append('\n');
//                output.append(tab).append(">parent\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">expr\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case CONDITIONAL: {
//                /*
//                CONDITIONAL endId (... => condition) END endId (... => ifBranch) END endId (... => elseBranch) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("CONDITIONAL endId=").append(endId).append('\n');
//                output.append(tab).append(">condition\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">ifBranch\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">elseBranch\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case FOREACH: {
//                /*
//                FOREACH variant endId (... => iterable) END endId (... => action) END endId
//                 */
//                String variant = input.readUTF();
//                int endId = input.readInt();
//                output.append(tab).append("FOREACH variant=").append(variant).append(" endId=").append(endId).append('\n');
//                output.append(tab).append(">iterable\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">action\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case ARRAY: {
//                /*
//                ARRAY endId (... => childs) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("ARRAY endId=").append(endId).append('\n');
//                output.append(tab).append(">childs\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case DICTIONARY: {
//                /*
//                DICTIONARY endId (... => (key,value)*) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("DICTIONARY endId=").append(endId).append('\n');
//                output.append(tab).append(">kvpairs\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case RANGE: {
//                /*
//                RANGE endId (... => left) END endId (... => right) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("RANGE endId=").append(endId).append('\n');
//                output.append(tab).append(">left\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">right\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case UNDEFINED: {
//                /*
//                UNDEFINED
//                 */
//                output.append(tab).append("UNDEFINED\n");
//                return true;
//            }
//            case BOOLEAN: {
//                /*
//                BOOLEAN bool
//                 */
//                output.append(tab).append("BOOLEAN bool=").append(input.readBoolean()).append('\n');
//                return true;
//            }
//            case INT: {
//                /*
//                INT int
//                 */
//                output.append(tab).append("INT int=").append(input.readInt()).append('\n');
//                return true;
//            }
//            case DECIMAL: {
//                /*
//                DECIMAL decimal
//                 */
//                output.append(tab).append("DECIMAL decimal=").append(input.readDouble()).append('\n');
//                return true;
//            }
//            case STRING: {
//                /*
//                STRING string
//                 */
//                output.append(tab).append("STRING string=").append(input.readUTF()).append('\n');
//                return true;
//            }
//            case FUNCTION: {
//                /*
//                FUNCTION name endId (... => parameters) END endId (... => expr) END endId
//                 */
//                String name = input.readUTF();
//                int endId = input.readInt();
//
//                output.append(tab).append("FUNCTION name=").append(name.isEmpty() ? null : name).append(" endId=").append(endId).append('\n');
//                output.append(tab).append(">parameters\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">expr\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case FUNCTION_PARAM: {
//                /*
//                FUNCTION_PARAM name modifiers endId (... => typeExpr) END endId [modifiers.hasDefaultValue ? (... => defaultValue) END endId]
//                 */
//                String name = input.readUTF();
//                int modifiers = input.readInt();
//                int endId = input.readInt();
//
//                output.append(tab).append("FUNCTION_PARAM name=").append(name).append(" modifiers=").append(modifiers).append(" endId=").append(endId).append(
//                        '\n');
//                output.append(tab).append(">typeExpr\n");
//                collectUntil(input, output, tab + "    ", endId);
//                if ((modifiers & 1) == 1) {
//                    output.append(tab).append(">defaultValue\n");
//                    collectUntil(input, output, tab + "    ", endId);
//                }
//                return true;
//            }
//            case IDENTIFIER: {
//                /*
//                IDENTIFIER parented name [parented ? endId (... => parent) END endId]
//                 */
//                boolean parented = input.readBoolean();
//                String name = input.readUTF();
//
//                output.append(tab).append("IDENTIFIER parented=").append(parented).append(" name=").append(name);
//
//                if (!parented) {
//                    output.append('\n');
//                    return true;
//                }
//
//                int endId = input.readInt();
//                output.append(" endId=").append(endId).append('\n');
//                output.append(tab).append(">parent\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case INVOCATION: {
//                /*
//                INVOCATION endId (... => left) END endId (... => arguments) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("INVOCATION endId=").append(endId).append('\n');
//                output.append(tab).append(">left\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">arguments\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case UNARY_OP: {
//                /*
//                UNARY_OP type endId (... => operand) END endId
//                 */
//                int type = input.readInt(), endId = input.readInt();
//                output.append(tab).append("UNARY_OP type=").append(type)
//                        .append("(").append(UnaryOperatorType.values()[type]).append(")")
//                        .append(" endId=").append(endId).append('\n');
//                output.append(tab).append(">target\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case BINARY_OP: {
//                /*
//                BINARY_OP type endId (... => left) END endId (... => right) END endId
//                 */
//                int type = input.readInt(), endId = input.readInt();
//                output.append(tab).append("UNARY_OP type=").append(type)
//                        .append("(").append(BinaryOperatorType.values()[type]).append(")")
//                        .append(" endId=").append(endId).append('\n');
//                output.append(tab).append(">left\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">right\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case SLICE_OP: {
//                /*
//                SLICE_OP endId (... => obj) END endId (... => start) END endId (... => end) END endId (... => step) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("SLICE_OP endId=").append(endId).append('\n');
//                output.append(tab).append(">obj\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">start\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">end\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">step\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case GET: {
//                /*
//                GET endId (... => left) END endId (... => key) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("GET endId=").append(endId).append('\n');
//                output.append(tab).append(">left\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">key\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            case SET: {
//                /*
//                SET endId (... => left) END endId (... => key) END endId (... => value) END endId
//                 */
//                int endId = input.readInt();
//                output.append(tab).append("SET endId=").append(endId).append('\n');
//                output.append(tab).append(">left\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">key\n");
//                collectUntil(input, output, tab + "    ", endId);
//                output.append(tab).append(">value\n");
//                collectUntil(input, output, tab + "    ", endId);
//                return true;
//            }
//            default: {
//                output.append(tab).append("UNKNOWN(").append(bytecode).append(")\n");
//                return true;
//            }
//        }
//    }
//}
