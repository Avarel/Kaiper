package xyz.avarel.kaiper;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.bytecode.KaiperBytecode;
import xyz.avarel.kaiper.bytecode.io.KDataOutputStream;
import xyz.avarel.kaiper.compiler.ExprCompiler;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.KaiperVM;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class KaiperCompiler {
    public static byte[] compile(Expr expr) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compile(expr, stream);
        return stream.toByteArray();
    }

    public static void compile(Expr expr, OutputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        KDataOutputStream out = new KDataOutputStream(buffer); //Underlaying Stream = buffer

        ExprCompiler compiler = new ExprCompiler();
        expr.accept(compiler, out);

        out.setOutputStream(stream); //Underlaying Stream = stream

        KaiperBytecode.initialize(out);
        compiler.writeStringPool(out);
        out.write(buffer.toByteArray());
        KaiperBytecode.finalize(out);
    }

    public static void compileCompressed(Expr expr, OutputStream stream) throws IOException {
        try (GZIPOutputStream gz = new GZIPOutputStream(stream)) {
            compile(expr, gz);
        }
    }

    public static byte[] compileCompressed(Expr expr) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compileCompressed(expr, stream);
        return stream.toByteArray();
    }

    private final ExprParser parser;
    private final Scope scope;
    private byte[] compiledBytes;

    public KaiperCompiler(String script) {
        this(script, DefaultScope.INSTANCE.copy());
    }

    public KaiperCompiler(Reader reader) {
        this(reader, DefaultScope.INSTANCE.copy());
    }

    public KaiperCompiler(String script, Scope scope) {
        this(new KaiperLexer(script), scope);
    }

    public KaiperCompiler(Reader reader, Scope scope) {
        this(new KaiperLexer(reader), scope);
    }

    public KaiperCompiler(KaiperLexer lexer, Scope scope) {
        this(new ExprParser(lexer), scope);
    }

    public KaiperCompiler(ExprParser parser, Scope scope) {
        this.parser = parser;
        this.scope = scope;
    }

    public byte[] compile() {
        return compile(true);
    }

    public byte[] compile(boolean compressed) {
        try {
            if (compiledBytes == null) {
                Expr expr = parser.parse();
                compiledBytes = compressed ? compileCompressed(expr) : compile(expr);
            }
            return compiledBytes;
        } catch (UncheckedIOException e) {
            throw new KaiperException(e.getCause());
        } catch (IOException e) {
            throw new KaiperException(e);
        }
    }

    public Obj compute() {
        try {
            return new KaiperVM().executeBytecode(compile(), scope);
        } catch (ReturnException e) {
            return e.getValue();
        } catch (UncheckedIOException e) {
            throw new KaiperException(e.getCause());
        } catch (IOException e) {
            throw new KaiperException(e);
        }
    }

    public ExprParser getParser() {
        return parser;
    }

    public Scope getScope() {
        return scope;
    }
}
