package xyz.hexav.aje;

import xyz.hexav.aje.expressions.AJELexer;
import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.operators.OperatorMap;
import xyz.hexav.aje.operators.Precedence;
import xyz.hexav.aje.pool.Pool;
import xyz.hexav.aje.types.*;

/**
 * Compiles string expressions into compiled Expression instances.
 */
public class ExpressionCompiler {
    private final Pool pool;
    private final AJELexer lexer;

    public ExpressionCompiler(Pool pool, String s) {
        this.pool = pool;
        this.lexer = new AJELexer(s);
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
        int start = lexer.pos();

        if (!Character.isLetter(lexer.currentChar())) {
            throw makeError("Nominal literals must start with a letter.");
        }

        while (isLiteral(lexer.currentChar())) lexer.nextChar();
        return lexer.target().substring(start, lexer.pos());
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

    public OperableValue compute() {
        lexer.nextChar();
        OperableValue e = compileExpression();

//        while (lexer.consume(';') && lexer.pos() < lexer.target().length()) {
//            e = e.andThen(compileExpression());
//        }

        if (lexer.pos() < lexer.target().length()) throw makeError("Unhandled character `" + lexer.currentChar() + "`.");

        return e;
    }

//    private Expression compileVariable() {
//        if (isLiteral(lexer.currentChar())) {
//            String name = nextLiteral();
//
//            if (pool.hasVar(name)) {
//                throw makeError("Variable '" + name + "' is already defined.");
//            }
//
//            if (!lexer.consume('=')) {
//                return new VariableAssignment(pool.allocVar(name));
//            } else {
//                return new VariableAssignment(pool.allocVar(name), compileExpression());
//            }
//
//        }
//        throw makeError("Expected name for variable.");
//    }
//
//    private Expression compileValue() {
//        if (isLiteral(lexer.currentChar())) {
//            String name = nextLiteral();
//
//            if (pool.hasVar(name)) {
//                throw makeError("Value '" + name + "' is already defined.");
//            }
//
//            if (!lexer.consume('=')) {
//                return new VariableAssignment(pool.allocVal(name));
//            } else {
//                Expression e = compileExpression();
//                return new VariableAssignment(pool.allocVal(name), e);
//            }
//        }
//        throw makeError("Expected name for value.");
//    }

//    private void consumeFunction() {
//        if (isLiteral(lexer.currentChar())) {
//            String name = nextLiteral();
//            int params = 0;
//
//            FunctionBuilder fb = new FunctionBuilder(name);
//
//            if (!lexer.consume('(')) {
//                throw makeError("Expected '('.");
//            }
//
//            if (!lexer.consume(')')) do {
//                fb.addParameter(nextLiteral());
//                params++;
//            }
//            while (!lexer.consume(')') && lexer.consume(','));
//
//            if (pool.hasFunc(name, params)) {
//                throw makeError("Function '" + name + "' is already defined.");
//            }
//
//            if (!lexer.consume('=')) {
//                throw makeError("Expected '='.");
//            }
//            lexer.skipWhitespace();
//
//            int start = lexer.pos();
//            while (!lexer.nextIs(';') && lexer.pos() < lexer.target().length()) {
//                lexer.nextChar();
//            }
//            String script = lexer.target().substring(start, lexer.pos());
//
//            fb.addLine(script);
//
//            pool.allocFunc(fb.build());
//
//            return;
//        }
//        throw makeError("Expected function name.");
//    }

    private OperableValue compileExpression() {
        return compileExpression(Precedence.LOGICAL_OR);
    }

    private OperableValue compileExpression(int level) {
        OperatorMap operators = pool.getOperators();

        //System.out.print(level + " ");

        if (operators.get(level) == null) {
            return compileLiteral();
        }


        // Unary operations
        for (Operator operator : operators.get(level)) {
            if (operator.getArgs() == -1 && lexer.consume(operator.getSymbol())) {
                OperableValue exp = compileExpression(level);
                return operator.apply(exp, null);
            }
        }

        OperableValue value = compileExpression(level + 1);

        while (true) {
            boolean flag = false;
            for (Operator operator : operators.get(level)) {
               // System.out.print(operator.getSymbol() + " ");
                if (operator.getArgs() == 2 && lexer.consume(operator.getSymbol())) {
                    final OperableValue a = value;
                    final OperableValue b = compileExpression(operator.isLeftAssoc() ? level + 1 : level); // operator.isLeftAssoc() ? Precedence.UNARY :  level + 1

                    //System.out.print(operator + " ");

                    value = operator.apply(a, b);
                    flag = true;
                } else if (operator.getArgs() == 1 && lexer.consume(operator.getSymbol())) {
                    value = operator.apply(value, null);
                    flag = true;
                }
            }
            if (!flag) break;
        }

        return value;
    }

    private OperableValue compileLiteral() {
        if (lexer.consume('(')) {
            OperableValue value = compileExpression();
            lexer.consume(')');
            return value;
        }

        while (lexer.currentChar() == ';') lexer.nextChar();
        if (lexer.pos() == lexer.target().length()) return Nothing.VALUE;

        int start = lexer.pos();

        // DEFINE VALUES
        // - NUMERICAL PARSING

//        if (lexer.consume("var")) {
//            return compileVariable();
//        } else if (lexer.consume("val")) {
//            return compileValue();
//        }
        if (isNumeric(lexer.currentChar())) {
            // BINARY
            if (lexer.consume("0b")) {
                int _start = lexer.pos();

                while (!isNumeric(lexer.currentChar())) {
                    if (!(lexer.currentChar() == '0' || lexer.currentChar() == '1')) {
                        throw makeError("Binary literals can only have '1' and '0'.");
                    }
                    lexer.nextChar();
                }

                double value = Integer.valueOf(lexer.target().substring(_start, lexer.pos()), 2).doubleValue();
                return new Numeric(value);
            }
            // HEXADECIMAL
            else if (lexer.consume("0x")) {
                int _start = lexer.pos();

                while (isLiteral(lexer.currentChar())) {
                    if (!(lexer.currentChar() >= '0' && lexer.currentChar() <= '9'
                            || lexer.currentChar() >= 'A' && lexer.currentChar() <= 'F')) {
                        throw makeError("Hexadecimal literals can only have '1-9' and 'A-F'.");
                    }
                    lexer.nextChar();
                }

                double value = Integer.valueOf(lexer.target().substring(_start, lexer.pos()), 16).doubleValue();
                return new Numeric(value);
            }

            while (!lexer.nextIs("..") && isNumeric(lexer.currentChar())) lexer.nextChar();

            double value = Double.parseDouble(lexer.target().substring(start, lexer.pos()));

            if (lexer.consume('i')) {
                return new Complex(0, value);
            }

            return new Numeric((value));
        }
        else if (lexer.consume('i')) {
            return new Complex(0, 1);
        }
//        // LISTS
//        else if (lexer.consume('[')) {
//
//            if (lexer.consume(']')) return Slice.EMPTY;
//
//            Slice list = new Slice();
//
//            do {
//                Expression a = compileExpression();
//
////                if (lexer.consume("..")) {
////                    Expression b = compileExpression();
////                    Expression c = list.size() == 1 ? list.get(list.size() - 1) : null;
////                    list.add(new Range(a, b, c));
////                } else {
//                    list.add(a);
////                }
//            }
//            while (lexer.consume(','));
//
//            if (!lexer.consume(']')) throw makeError("Expected list literal to close.");
//
//            return list;
//        }
        else if (lexer.consume("true")) {
            return Truth.TRUE;
        } else if (lexer.consume("false")) {
            return Truth.FALSE;
        }

        // FUNCTION AND CONSTANTS
        else if (Character.isLetter(lexer.currentChar())) {
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
//            if (pool.hasVar(name)) {
//                return pool.getVar(name);
//            }
//            else {
//                throw makeError("Can not resolve reference `" + name + "`.");
//            }
        }
        throw makeError("Unexpected token: " + lexer.currentChar());
    }

    private AJEException makeError(String message) {
        return new AJEException(message + " " + lexer.currentInfo());
    }
}
