package xyz.avarel.kaiper.bytecode;

import xyz.avarel.kaiper.KaiperCompiler;
import xyz.avarel.kaiper.KaiperScript;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.tools.outliner.BytecodeOutliner;
import xyz.avarel.kaiper.tools.outliner.OutlineOptions;

import java.io.IOException;

public class KaiperBytecodeTest {
    public static void main(String[] args) throws IOException {
        // Base expression.
        String code = "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]";
        System.out.println("Code: " + code);
        System.out.println();
        KaiperScript exp = new KaiperScript(code);
        Expr expCompiled = exp.compile();

        byte[] bytecode = KaiperCompiler.compile(expCompiled);

        System.out.println("Bytecode: " + bytecode.length + " bytes");

        byte[] compressedBytecode = KaiperCompiler.compileCompressed(expCompiled);

        System.out.println("Compressed Bytecode: "  + compressedBytecode.length + " bytes");

        System.out.println();

        System.out.println("[=-=-=- Kaiper Bytecode Viewer -=-=-=]");

        new BytecodeOutliner(
                new OutlineOptions()
                    .skipStringPool(true)
                    .inlineStrings(false)
        ).doOutlineCompressed(compressedBytecode, System.out);

        System.out.println("[=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=]");
    }
}
