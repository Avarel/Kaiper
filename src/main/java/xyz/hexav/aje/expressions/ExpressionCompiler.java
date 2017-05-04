package xyz.hexav.aje.expressions;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.FunctionBuilder;
import xyz.hexav.aje.defaults.DefaultOperators;
import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.operators.OperatorMap;
import xyz.hexav.aje.pool.Pool;
import xyz.hexav.aje.types.AJEList;
import xyz.hexav.aje.types.AJENothing;
import xyz.hexav.aje.types.AJERange;
import xyz.hexav.aje.types.AJEValue;

/**
 * Compiles string expressions into compiled Expression instances.
 */
public class ExpressionCompiler {
    private final Pool pool;
    private final Tokenizer tokenizer;

    public ExpressionCompiler(Pool pool, String s) {
        this.pool = pool;
        this.tokenizer = new Tokenizer(s);
    }

    private static boolean isLiteral(char c) {
        return Character.isLetter(c) || c >= '0' && c <= '9' || c == '_';
    }

    private static boolean isNumeric(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }

    /**
     * Return the next String name.
     */
    private String nextLiteral() {
        int start = tokenizer.pos();

        if (!Character.isLetter(tokenizer.currentChar())) {
            throw makeError("Nominal literals must start with a letter.");
        }

        while (isLiteral(tokenizer.currentChar())) tokenizer.nextChar();
        return tokenizer.target().substring(start, tokenizer.pos());
    }
//
//    public List<Expression> compileScript(String script) {
//        return compileLines(toLines(script));
//    }


//    public List<Expression> compileScripts(List<String> scripts) {
//        List<String> lines = new ArrayList<>();
//
//        for (String script : scripts) {
//            lines.addAll(toLines(script));
//            pos = -1;
//        }
//
//        return compileLines(lines);
//    }

//    private List<String> toLines(String script) {
//        char s_current = script.charAt(++pos);
//
//        List<String> lines = new ArrayList<>();
//
//        do {
//            int start = pos;
//
//            while (s_current != ';' && ++pos < script.length()) {
//                s_current = script.charAt(pos);
//            }
//
//            lines.add(script.substring(start, pos).trim());
//
//            if (s_current != ';') break;
//            else s_current = ++pos < script.length() ? script.charAt(pos) : (char) -1;
//        }
//        while (pos < script.length());
//
//        return lines;
//    }

//    private List<Expression> compileLines(List<String> lines) {
//        List<Expression> exps = new ArrayList<>();
//
//        // Compile values and functions first.
//        for (String line : lines) {
//            resetPosition();
//            setStr(line);
//            nextChar();
//
//            if (consume("func")) {
//                consumeFunction();
//            } else {
//                exps.add(compileLine(line));
//            }
//        }
//
//        return exps;
//    }

    public AJEValue compile() {
        tokenizer.nextChar();
        AJEValue e = compileExpression();

        while (tokenizer.consume(';') && tokenizer.pos() < tokenizer.target().length()) {
            e = e.andThen(compileExpression());
        }

        if (tokenizer.pos() < tokenizer.target().length()) throw makeError("Unhandled character `" + tokenizer.currentChar() + "`.");

        return e;
    }

//    private Expression compileLine(String target) {
//        resetPosition();
//        nextChar();
//
//        Expression exp = compileExpression();
//
//        if (pos < str.length()) throw makeError("Unhandled character `" + current + "`.");
//
//        return exp;
//    }

    private AJEValue compileVariable() {
        if (isLiteral(tokenizer.currentChar())) {
            String name = nextLiteral();

            if (pool.hasVar(name)) {
                throw makeError("Variable '" + name + "' is already defined.");
            }

            if (!tokenizer.consume('=')) {
                return new VariableAssignment(pool.allocVar(name));
            } else {
                return new VariableAssignment(pool.allocVar(name), compileExpression());
            }


        }
        throw makeError("Expected name for variable.");
    }

    private AJEValue compileValue() {
        if (isLiteral(tokenizer.currentChar())) {
            String name = nextLiteral();

            if (pool.hasVar(name)) {
                throw makeError("Value '" + name + "' is already defined.");
            }

            if (!tokenizer.consume('=')) {
                return new VariableAssignment(pool.allocVal(name));
            } else {
                return new VariableAssignment(pool.allocVal(name), compileExpression());
            }
        }
        throw makeError("Expected name for value.");
    }

    private void consumeFunction() {
        if (isLiteral(tokenizer.currentChar())) {
            String name = nextLiteral();
            int params = 0;

            FunctionBuilder fb = new FunctionBuilder(name);

            if (!tokenizer.consume('(')) {
                throw makeError("Expected '('.");
            }

            if (!tokenizer.consume(')')) do {
                fb.addParameter(nextLiteral());
                params++;
            }
            while (!tokenizer.consume(')') && tokenizer.consume(','));

            if (pool.hasFunc(name, params)) {
                throw makeError("Function '" + name + "' is already defined.");
            }

            if (!tokenizer.consume('=')) {
                throw makeError("Expected '='.");
            }
            tokenizer.skipWhitespace();

            int start = tokenizer.pos();
            while (!tokenizer.nextIs(';') && tokenizer.pos() < tokenizer.target().length()) {
                tokenizer.nextChar();
            }
            String script = tokenizer.target().substring(start, tokenizer.pos());

            fb.addLine(script);

            pool.allocFunc(fb.build());

            return;
        }
        throw makeError("Expected function name.");
    }

    private AJEValue compileExpression() {
        return compileExpression(pool.getOperators().firstPrecedence());
    }

    private AJEValue compileExpression(int level) {
        if (level == -1) return compileMisc();

        OperatorMap operators = pool.getOperators();

        // Unary operations
        for (Operator operator : operators.get(level)) {
            //System.out.println(operator);
            if (operator.args == -1 && tokenizer.consume(operator.symbol)) {
                AJEValue exp = compileExpression(level);
                return operator.compile(exp);
            }
        }

        AJEValue value = compileExpression(operators.after(level));

        while (true) {
            boolean flag = false;
            for (Operator operator : operators.get(level)) {
                if (operator.args == 2 && tokenizer.consume(operator.symbol)) {
                    final AJEValue
                            a = value,
                            b = compileExpression(
                                    operator.next != -1 ?
                                            operator.next :
                                            operators.after(level));

                    value = operator.compile(a, b);

                    flag = true;
                } else if (operator.args == 1 && tokenizer.consume(operator.symbol)) {
                    value = operator.compile(value);
                    flag = true;
                }
            }
            if (!flag) break;
        }

        return value;
    }

    private AJEValue compileMisc() {
        AJEValue value;
        if (tokenizer.consume('(')) {
            value = compileExpression();
            tokenizer.consume(')');
        } else value = compileLiteral();

        // Implicit multiplications.
        if (tokenizer.nextIs('(') || isLiteral(tokenizer.currentChar())) {
            int _pos = tokenizer.pos();
            char _current = tokenizer.currentChar();
            try {
                final AJEValue
                        a = value,
                        b = compileMisc();

                value = DefaultOperators.MULTIPLY.get().compile(a, b);
            } catch (RuntimeException e) {
                tokenizer.setPos(_pos);
                tokenizer.setCurrentChar(_current);
            }
        }

        return value;
    }

    private AJEValue compileLiteral() {
        while (tokenizer.currentChar() == ';') tokenizer.nextChar();
        if (tokenizer.pos() == tokenizer.target().length()) return AJENothing.VALUE;

        int start = tokenizer.pos();

        // DEFINE VALUES
        // - NUMERICAL PARSING

        if (tokenizer.consume("var")) {
            return compileVariable();
        } else if (tokenizer.consume("val")) {
            return compileValue();
        }

        if (isNumeric(tokenizer.currentChar())) {
            // BINARY
            if (tokenizer.consume("0b")) {
                int _start = tokenizer.pos();

                while (!isNumeric(tokenizer.currentChar())) {
                    if (!(tokenizer.currentChar() == '0' || tokenizer.currentChar() == '1')) {
                        throw makeError("Binary literals can only have '1' and '0'.");
                    }
                    tokenizer.nextChar();
                }

                double value = Integer.valueOf(tokenizer.target().substring(_start, tokenizer.pos()), 2).doubleValue();
                return () -> value;
            }
            // HEXADECIMAL
            else if (tokenizer.consume("0x")) {
                int _start = tokenizer.pos();

                while (isLiteral(tokenizer.currentChar())) {
                    if (!(tokenizer.currentChar() >= '0' && tokenizer.currentChar() <= '9'
                            || tokenizer.currentChar() >= 'A' && tokenizer.currentChar() <= 'F')) {
                        throw makeError("Hexadecimal literals can only have '1-9' and 'A-F'.");
                    }
                    tokenizer.nextChar();
                }

                double value = Integer.valueOf(tokenizer.target().substring(_start, tokenizer.pos()), 16).doubleValue();
                return () -> value;
            }

            while (!tokenizer.nextIs("..") && isNumeric(tokenizer.currentChar())) tokenizer.nextChar();

            double value = Double.parseDouble(tokenizer.target().substring(start, tokenizer.pos()));
            return () -> value;
        }
        // LISTS
        else if (tokenizer.consume('[')) {

            if (tokenizer.consume(']')) return AJEList.EMPTY;

            AJEList list = new AJEList();

            do {
                AJEValue a = compileExpression();

                if (tokenizer.consume("..")) {
                    AJEValue b = compileExpression();
                    AJEValue c = list.size() == 1 ? list.get(list.size() - 1) : null;
                    list.add(new AJERange(a, b, c));
                } else if (tokenizer.consume("til")) {
                    AJEValue b = compileExpression();
                    AJEValue c = list.size() == 1 ? list.get(list.size() - 1) : null;
                    list.add(new AJERange(a, () -> b.value() - 1, c));
                } else {
                    list.add(a);
                }
            }
            while (tokenizer.consume(','));

            if (!tokenizer.consume(']')) throw makeError("Expected list literal to close.");

            return list;
        }

        // FUNCTION AND CONSTANTS
        else if (Character.isLetter(tokenizer.currentChar())) {
            String name = nextLiteral();


//
//            // FUNCTIONS LOOKUP
//            if (tokenizer.consume('(')) {
//                List<Expression> args = new ArrayList<>();
//                if (!tokenizer.consume(')')) do {
//                    Expression exp;
//
//                    if (tokenizer.consume('*')) {
//                        exp = compileExpression().spread();
//                    } else exp = compileExpression();
//                    args.add(exp);
//                }
//                while (!tokenizer.consume(')') && tokenizer.consume(','));
//
//                if (pool.hasFunc(name, args.size())) {
//                    return new FunctionExpression(pool.getFunc(name, args.size()), args);
//                } else {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append('(');
//                    for (int i = 0; i < args.size(); i++) {
//                        sb.append("number");
//                        if (i < args.size() - 1) {
//                            sb.append(", ");
//                        }
//                    }
//                    sb.append(')');
//                    throw makeError("Can not resolve function `" + name + sb + "`.");
//                }
//            } else
            if (pool.hasVar(name)) {
                return new VariableExpression(pool.getVar(name));
            } else {
                throw makeError("Can not resolve reference `" + name + "`.");
            }
        } else throw makeError("Unexpected token: " + tokenizer.currentChar());
    }

    private AJEException makeError(String message) {
        return new AJEException(message + " " + tokenizer.currentInfo());
    }
}
