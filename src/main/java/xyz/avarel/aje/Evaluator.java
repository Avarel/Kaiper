package xyz.avarel.aje;

import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;

import java.io.*;

public class Evaluator {
    private final Scope scope;
    private Obj answer;

    public Evaluator() {
        this(DefaultScope.INSTANCE.copy());
    }

    public Evaluator(Scope scope) {
        this.scope = scope;
        this.answer = Undefined.VALUE;
    }

    public Evaluator(Evaluator parent) {
        this(parent.scope.subPool());
    }

    public Obj eval(String script) {
        return eval(new StringReader(script));
    }

    public Obj eval(File file) throws FileNotFoundException {
        return eval(new FileReader(file));
    }

    public Obj eval(Reader reader) {
        return eval(new AJELexer(reader));
    }

    public Obj eval(AJELexer lexer) {
        return answer = new AJEParser(lexer).compile().accept(new ExprVisitor(), scope);
    }

    public Obj getAnswer() {
        return answer;
    }
}
