package xyz.avarel.kaiper;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.bytecode.KaiperBytecode;
import xyz.avarel.kaiper.bytecode.io.ByteDataOutput;
import xyz.avarel.kaiper.bytecode.io.IOUtils;
import xyz.avarel.kaiper.compiler.ExprCompiler;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.ParserFlags;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.KaiperVM;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class KaiperCompiler {
    private final KaiperParser parser;
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
        this(new KaiperParser(lexer), scope);
    }

    public KaiperCompiler(KaiperParser parser, Scope scope) {
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
        } catch (IOException e) {
            throw new KaiperException(e);
        }
    }

    public Obj compute() {
        try {
            return new KaiperVM().executeBytecode(compile(), scope);
        } catch (ReturnException e) {
            return e.getValue();
        } catch (IOException e) {
            throw new KaiperException(e);
        }
    }

    public KaiperParser getParser() {
        return parser;
    }

    public Scope getScope() {
        return scope;
    }

    public void setParserFlags(short flags) {
        parser.setParserFlags(new ParserFlags(flags));
    }

    public static byte[] compile(Expr expr) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compile(expr, stream);
        return stream.toByteArray();
    }

    public static void compile(Expr expr, OutputStream out) throws IOException {
        ByteDataOutput dataOutput = IOUtils.wrap(new DataOutputStream(out));

        KaiperBytecode.initialize(dataOutput);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ExprCompiler compiler = new ExprCompiler();
        expr.accept(compiler, IOUtils.wrap(new DataOutputStream(buffer)));

        compiler.writeStringPool(dataOutput);
        out.write(buffer.toByteArray());

        KaiperBytecode.finalize(dataOutput);
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
}
