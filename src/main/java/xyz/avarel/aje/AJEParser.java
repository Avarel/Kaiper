package xyz.avarel.aje;

import xyz.avarel.aje.operators.AJEBinaryOperator;
import xyz.avarel.aje.operators.AJEUnaryOperator;
import xyz.avarel.aje.operators.OperatorMap;
import xyz.avarel.aje.operators.Precedence;
import xyz.avarel.aje.pool.Pool;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.Value;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Nothing;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compiles string expressions into compiled Expression instances.
 */
public class AJEParser {
    private final Pool pool;
    private final AJELexer lexer;

    public AJEParser(Pool pool, String s) {
        this.pool = pool;
        this.lexer = new AJELexer(s);
    }

    private static boolean isLiteral(char c) {
        return Character.isLetter(c) || c >= '0' && c <= '9' || c == '_';
    }

    private static boolean isNumeric(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }


    public Value compute() {
        lexer.advance();
        AJEObject e = compileExpression();

        while (lexer.consume(';') && lexer.pos() < lexer.getStr().length()) {
            e = compileExpression();
        }

        if (lexer.pos() < lexer.getStr().length()) throw error("Unhandled character `" + lexer.currentChar() + "`.");

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

    private AJEObject compileExpression() {
        return compileExpression(Precedence.DISJUNCTION);
    }

    private AJEObject compileExpression(int prec) {
        OperatorMap operatorMaps = pool.getOperators();

        //System.out.print(level + " ");

        if (operatorMaps.getBinaries().get(prec) == null
                && pool.getOperators().getUnaries().get(prec) == null) {
            return compileLiteral();
        }

        // Unary operations
        for (AJEUnaryOperator operator : pool.getOperators().getUnaries().getOrDefault(prec, Collections.emptySet())) {
            if (lexer.consume(operator.getSymbol())) {
                AJEObject exp = compileExpression(prec);
                return operator.apply(exp);
            }
        }

        AJEObject value = compileExpression(prec + 1);

        while (true) {
            boolean flag = false;
            for (AJEBinaryOperator operator : operatorMaps.getBinaries().getOrDefault(prec, Collections.emptySet())) {
                if (lexer.consume(operator.getSymbol())) {
                    final AJEObject a = value;
                    final AJEObject b = compileExpression(operator.isLeftAssoc() ? prec + 1 : prec);

                    value = operator.compile(a, b);
                    flag = true;
                }
            }
            if (!flag) break;
        }

        return value;
    }

    private AJEObject compileLiteral() {
        if (lexer.consume('(')) {
            AJEObject value = compileExpression();
            lexer.consume(')');
            return value;
        }

        while (lexer.currentChar() == ';') lexer.advance();
        if (lexer.pos() == lexer.getStr().length()) return Nothing.VALUE;

        AJEObject obj = Nothing.VALUE;

        int start = lexer.pos();

        // DEFINE VALUES
        // - NUMERICAL PARSING

//        if (lexer.consume("var")) {
//            return compileVariable();
//        } else if (lexer.consume("val")) {
//            return compileValue();
//        }
        if (isNumeric(lexer.currentChar())) {
//            // BINARY
//            if (lexer.consume("0b")) {
//                int _start = lexer.pos();
//
//                while (!isNumeric(lexer.currentChar())) {
//                    if (!(lexer.currentChar() == '0' || lexer.currentChar() == '1')) {
//                        throw error("Binary literals can only have '1' and '0'.");
//                    }
//                    lexer.advance();
//                }
//
//                int value = Integer.parseInt(lexer.getStr().substring(_start, lexer.pos()), 2);
//                return new Int(value);
//            }
//            // HEXADECIMAL
//            else if (lexer.consume("0x")) {
//                int _start = lexer.pos();
//
//                while (isLiteral(lexer.currentChar())) {
//                    if (!(lexer.currentChar() >= '0' && lexer.currentChar() <= '9'
//                            || lexer.currentChar() >= 'A' && lexer.currentChar() <= 'F')) {
//                        throw error("Hexadecimal literals can only have '1-9' and 'A-F'.");
//                    }
//                    lexer.advance();
//                }
//
//                int value = Integer.parseInt(lexer.getStr().substring(_start, lexer.pos()), 16);
//                return Int.of(value);
//            } else
            {
                while (!lexer.nextIs("..") && isNumeric(lexer.currentChar())) lexer.advance();

                String str = lexer.getStr().substring(start, lexer.pos()).trim();

                if (lexer.consume('i')) {
                    obj = Complex.of(0, Double.parseDouble(str));
                } else if (!str.contains(".")) {
                    obj = Int.of(Integer.parseInt(str));
                } else {
                    obj = Decimal.of(Double.parseDouble(str));
                }
            }
        }
        else if (lexer.consume('i')) {
            obj = Complex.of(0, 1);
        }
        // LISTS
        else if (lexer.consume('[')) {
            if (lexer.consume(']')) return Slice.EMPTY;

            Slice list = new Slice();

            do {
                AJEObject value = compileExpression();
                list.add(value);
            }
            while (lexer.consume(','));

            if (!lexer.consume(']')) throw error("Expected list literal to close.");

            obj = list;
        }

        else if (lexer.consume("true")) {
            obj = Truth.TRUE;
        } else if (lexer.consume("false")) {
            obj = Truth.FALSE;
        }

        // FUNCTION AND CONSTANTS
        else if (Character.isLetter(lexer.currentChar())) {
            String name = lexer.nextString();

            obj = pool.get(name);

            if (obj == null) {
                throw error("Could not resolve reference `" + name + "`.");
            }

        } else {
            throw error("Unexpected token: " + lexer.currentChar());
        }

        if (lexer.consume('(')) {
            List<AJEObject> args = new ArrayList<>();

            if (!lexer.consume(')')) {

                do {
                    args.add(compileExpression());
                } while (lexer.consume(','));

                if (!lexer.consume(')')) {
                    throw error("expected invocation to close");
                }
            }
            obj = obj.invoke(args);
        }

        return obj;
    }

    private AJEException error(String message) {
        return new AJEException(message + " " + lexer.currentInfo());
    }
}
