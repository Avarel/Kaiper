package xyz.avarel.aje.bytecode;

import xyz.avarel.aje.AJECompiler;
import xyz.avarel.aje.Expression;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.tools.bytecode.BytecodeOutliner;
import xyz.avarel.aje.tools.bytecode.OutlineOptions;

import java.io.IOException;

public class AJEBytecodeTest {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String[] args) throws IOException {
        // Base expression.
        String code = "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]";
        System.out.println("Code: " + code);
        System.out.println();
        Expression exp = new Expression(code);
        Expr expCompiled = exp.compile();

        byte[] bytecode = new AJECompiler().compile(expCompiled);

        System.out.println("Bytecode: " + bytecode.length + " bytes");

        byte[] compressedBytecode = new AJECompiler().compileCompressed(expCompiled);

        System.out.println("Compressed Bytecode: "  + compressedBytecode.length + " bytes");

        System.out.println();

        System.out.println("[=-=-=- AJE Bytecode Viewer -=-=-=]");

        new BytecodeOutliner(
                new OutlineOptions()
                    .skipStringPool(true)
                    .inlineStrings(false)
        ).doOutlineCompressed(compressedBytecode, System.out);

        System.out.println("[=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=]");
    }
}
