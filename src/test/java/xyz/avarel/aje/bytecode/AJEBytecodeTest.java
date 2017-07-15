//package xyz.avarel.aje.bytecode;
//
//import xyz.avarel.aje.BytecodeHelper;
//import xyz.avarel.aje.CompiledExpr;
//import xyz.avarel.aje.Expression;
//import xyz.avarel.aje.ast.Expr;
//import xyz.avarel.aje.oldbytecode.viewer.ExprViewer;
//import xyz.avarel.aje.scope.DefaultScope;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.IOException;
//
//public class AJEBytecodeTest {
//    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
//
//    public static String bytesToHex(byte[] bytes) {
//        char[] hexChars = new char[bytes.length * 2];
//        for (int j = 0; j < bytes.length; j++) {
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 2] = hexArray[v >>> 4];
//            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//        }
//        return new String(hexChars);
//    }
//
//    public static void main(String[] args) throws IOException {
//        // Base expression.
//        Expression exp = new Expression("let x = 2; x = 3; x");
//        Expr expCompiled = exp.compile();
//
//        StringBuilder b1 = new StringBuilder("Original AST:\n");
//        expCompiled.ast(b1, "", true);
//        System.out.println(b1);
//
//        System.out.println();
//
//        System.out.println("Original Result: " + exp.compute().toJava());
//
//        System.out.println();
//
//
//        byte[] bytecode = BytecodeHelper.toBytecode(expCompiled);
//
//        System.out.println("Bytecode: " + bytesToHex(bytecode));
//
//        System.out.println();
//
//        System.out.println("AJE Bytecode Viewer:\n");
//
//        System.out.println(
//                ExprViewer.view(
//                        new DataInputStream(
//                                new ByteArrayInputStream(
//                                        bytecode
//                                )
//                        )
//                )
//        );
//
//        System.out.println();
//
//
//        CompiledExpr expr = new CompiledExpr(DefaultScope.INSTANCE.copy(), BytecodeHelper.fromBytecode(bytecode));
//
//        StringBuilder b2 = new StringBuilder("AST Now:\n");
//        expr.ast(b2, "", true);
//        System.out.println(b2);
//
//        System.out.println();
//
//        System.out.println("Result Now: " + expr.compute().toJava());
//    }
//}
