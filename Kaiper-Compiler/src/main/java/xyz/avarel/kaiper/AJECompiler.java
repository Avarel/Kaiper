package xyz.avarel.kaiper;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.bytecode.AJEBytecode;
import xyz.avarel.kaiper.bytecode.DataOutputConsumer;
import xyz.avarel.kaiper.compiler.ExprCompiler;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class AJECompiler {
    public void compile(Expr expr, DataOutput out) throws IOException {
        AJEBytecode.initialize(out);

        ExprCompiler compiler = new ExprCompiler();
        DataOutputConsumer bytecode = expr.accept(compiler, null);

        compiler.stringPool().writeInto(out);
        bytecode.writeInto(out);

        AJEBytecode.finalize(out);
    }

    public void compile(Expr expr, OutputStream out) throws IOException {
        compile(expr, (DataOutput) new DataOutputStream(out));
    }

    public byte[] compile(Expr expr) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compile(expr, stream);
        return stream.toByteArray();
    }

    public void compileCompressed(Expr expr, OutputStream stream) throws IOException {
        try (GZIPOutputStream gz = new GZIPOutputStream(stream)) {
            compile(expr, gz);
        }
    }

    public byte[] compileCompressed(Expr expr) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compileCompressed(expr, stream);
        return stream.toByteArray();
    }
}
