//package xyz.avarel.aje;
//
//import xyz.avarel.aje.ast.Expr;
//import xyz.avarel.aje.compiler.AJEBytecode;
//import xyz.avarel.aje.oldbytecode.deserialization.ExprDeserializer;
//import xyz.avarel.aje.compiler.DataOutputConsumer;
//import xyz.avarel.aje.oldbytecode.serialization.ExprSerializer;
//
//import java.io.*;
//
//public class BytecodeHelper {
//    public static byte[] toBytecode(Expr expr) throws IOException {
//        DataOutputConsumer consumer = expr.accept(new ExprSerializer(), null);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//        DataOutput output = AJEBytecode.initialize(new DataOutputStream(stream));
//        consumer.writeInto(output);
//        AJEBytecode.finalize(output);
//
//        return stream.toByteArray();
//    }
//
//    public static Expr fromBytecode(byte[] bytecode) throws IOException {
//        return new ExprDeserializer(
//                new DataInputStream(
//                        new ByteArrayInputStream(
//                                bytecode
//                        )
//                )
//        ).deserializeAll();
//    }
//}
