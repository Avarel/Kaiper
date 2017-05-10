package xyz.avarel.aje;

import xyz.avarel.aje.pool.Pool;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.compiled.CompiledFunction;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;
import xyz.avarel.aje.types.others.Undefined;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Compiles string expressions into compiled Expression instances.
 */
@SuppressWarnings("unchecked")
public class AJEParser extends RecursiveDescentParser<Any> {
    private final Pool pool;
    private final AJELexer lexer;

    public AJEParser(Pool pool, String s) {
        this.pool = pool;
        this.lexer = new AJELexer(s);

        addProcedure((self, parser) -> {
            Any value = next(self);
            while (true) {
                if (lexer.consume("==")) {
                    value = process(value, next(self), Any::equals);
                } else if (lexer.consume(">=")) {
                    value = process(value, next(self), Any::greaterThanOrEqual);
                } else if (lexer.consume("<=")) {
                    value = process(value, next(self), Any::lessThanOrEqual);
                } else if (lexer.consume('>')) {
                    value = process(value, next(self), Any::greaterThan);
                } else if (lexer.consume('<')) {
                    value = process(value, next(self), Any::lessThan);
                } else break;
            }
            return value;
        });

        addProcedure((self, parser) -> {
            Any value = next(self);
            while (true) {
                if (lexer.consume("..")) {
                    value = process(value, next(self), Any::rangeTo);
                } else break;
            }
            return value;
        });

        addProcedure((self, parser) -> {
            Any value = next(self);
            while (true) {
                if (lexer.consume('+')) {
                    value = process(value, next(self), Any::plus);
                } else if (lexer.consume('-')) {
                    value = process(value, next(self), Any::minus);
                } else break;
            }
            return value;
        });

        addProcedure((self, parser) -> {
            Any value = next(self);
            while (true) {
                if (lexer.consume('*')) {
                    value = process(value, next(self), Any::times);
                } else if (lexer.consume('/')) {
                    value = process(value, next(self), Any::divide);
                } else if (lexer.consume('%')) {
                    value = process(value, next(self), Any::mod);
                } else break;
            }
            return value;
        });

        addProcedure(((self, parser) -> {
            while (lexer.currentChar() == ';') lexer.advance();
            if (lexer.pos() >= lexer.getStr().length()) return Undefined.VALUE;

            Any<?> any;

            int start = lexer.pos();

            lexer.skipWhitespace();
            if (lexer.consume('(')) {
                Any value = go(0);
                lexer.consume(')');
                any = value;
            } else if (lexer.consume("fun")) {
                any = compileFunction();
            } else if (isNumeric(lexer.currentChar())) {
                while (!lexer.peek("..") && isNumeric(lexer.currentChar())) lexer.advance();

                String str = lexer.getStr().substring(start, lexer.pos()).trim();

                if (lexer.consume('i')) {
                    any = Complex.of(0, Double.parseDouble(str));
                } else if (!str.contains(".")) {
                    any = Int.of(Integer.parseInt(str));
                } else {
                    any = Decimal.of(Double.parseDouble(str));
                }
            } else if (lexer.consume('i')) {
                any = Complex.of(0, 1);
            } else if (lexer.consume('[')) {
                if (lexer.consume(']')) return Slice.EMPTY;

                Slice list = new Slice();

                do {
                    Any value = parser.go(0);
                    list.add(value);
                }
                while (lexer.consume(','));

                if (!lexer.consume(']')) throw error("Expected list literal to close.");

                any = list;
            } else if (lexer.consume("true")) {
                any = Truth.TRUE;
            } else if (lexer.consume("false")) {
                any = Truth.FALSE;
            } else if (Character.isLetter(lexer.currentChar())) {
                String name = lexer.nextString();

                any = pool.get(name);

                if (any == null) {
                    any = Undefined.VALUE;
                }
            } else {
                throw error("Unexpected token: " + lexer.currentChar());
            }

            if (lexer.consume('(')) {
                List<Any> args = new ArrayList<>();

                if (!lexer.consume(')')) {

                    do {
                        args.add(parser.go(0));
                    } while (lexer.consume(','));

                    if (!lexer.consume(')')) {
                        throw error("expected invocation to close");
                    }
                }

                any = any.invoke(args);
            }

            return any;
        }));
    }

    public Pool getPool() {
        return pool;
    }

    private static boolean isLiteral(char c) {
        return Character.isLetter(c) || c >= '0' && c <= '9' || c == '_';
    }

    private static boolean isNumeric(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }


    public Any compute() {
        Any e = parse();

        while (lexer.consume(';') && lexer.pos() < lexer.getStr().length()) {
            e = parse();
        }

        lexer.skipWhitespace();

        if (lexer.pos() < lexer.getStr().length()) throw error("Unhandled character `" + lexer.currentChar() + "`.");

        return e;
    }
//
////    private Expression compileVariable() {
////        if (isLiteral(lexer.currentChar())) {
////            String name = nextLiteral();
////
////            if (pool.hasVar(name)) {
////                throw makeError("Variable '" + name + "' is already defined.");
////            }
////
////            if (!lexer.consume('=')) {
////                return new VariableAssignment(pool.allocVar(name));
////            } else {
////                return new VariableAssignment(pool.allocVar(name), compileExpression());
////            }
////
////        }
////        throw makeError("Expected name for variable.");
////    }
////
////    private Expression compileValue() {
////        if (isLiteral(lexer.currentChar())) {
////            String name = nextLiteral();
////
////            if (pool.hasVar(name)) {
////                throw makeError("Value '" + name + "' is already defined.");
////            }
////
////            if (!lexer.consume('=')) {
////                return new VariableAssignment(pool.allocVal(name));
////            } else {
////                Expression e = compileExpression();
////                return new VariableAssignment(pool.allocVal(name), e);
////            }
////        }
////        throw makeError("Expected name for value.");
////    }
//
    private CompiledFunction compileFunction() {
        lexer.skipWhitespace();
//        if (isLiteral(lexer.currentChar())) {
//            String name = lexer.nextString();

            List<String> parameters = new ArrayList<>();

            if (!lexer.consume('(')) throw error("srsly");

            if (!lexer.consume(')')) {
                do {
                    String paramName = lexer.nextString();
                    parameters.add(paramName);
                } while (lexer.consume(','));

                if (!lexer.consume(')')) throw error("bro");
            }


            if (!lexer.consume('{')) throw error("open sesame");

            int start = lexer.pos();

            if (!lexer.skipTo('}')) throw error("close sesame");
            int end = lexer.pos();
            lexer.consume('}');

            String script = lexer.getStr().substring(start, end);

            return new CompiledFunction(parameters, script);
//        }
//        throw error("what");
    }



//    private Any compileExpression() {
//        return compileExpression(Precedence.DISJUNCTION);
//    }
//
//    private Any compileExpression(int prec) {
//        OperatorMap operatorMaps = pool.getOperators();
//
//        //System.out.print(level + " ");
//
//        if (operatorMaps.binary().get(prec) == null
//                && pool.getOperators().prefix().get(prec) == null) {
//            return compileLiteral();
//        }
//
//        // Unary operations
//        for (AJEUnaryOperator operator : pool.getOperators().prefix().getOrDefault(prec, Collections.emptySet())) {
//            if (lexer.consume(operator.getSymbol())) {
//                Any exp = compileExpression(prec);
//                return operator.apply(exp);
//            }
//        }
//
//        Any value = compileExpression(prec + 1);
//
//        while (true) {
//            boolean flag = false;
//            for (AJEBinaryOperator operator : operatorMaps.binary().getOrDefault(prec, Collections.emptySet())) {
//                if (lexer.consume(operator.getSymbol())) {
//                    final Any a = value;
//                    final Any b = compileExpression(operator.isLeftAssoc() ? prec + 1 : prec);
//
//                    value = operator.compile(a, b);
//                    flag = true;
//                }
//            }
//            if (!flag) break;
//        }
//
//        return value;
//    }
//
//    private Any compileLiteral() {
//        while (lexer.currentChar() == ';') lexer.advance();
//        if (lexer.pos() >= lexer.getStr().length()) return Undefined.VALUE;
//
//        Any any;
//
//        int start = lexer.pos();
//
//        // DEFINE VALUES
//        // - NUMERICAL PARSING
//
////        if (lexer.consume("var")) {
////            return compileVariable();
//        if (lexer.consume('(')) {
//            Any value = compileExpression();
//            lexer.consume(')');
//            any = value;
//        } else if (lexer.consume("fun")) {
//            any = compileFunction();
//        } else if (isNumeric(lexer.currentChar())) {
////            // BINARY
////            if (lexer.consume("0b")) {
////                int _start = lexer.pos();
////
////                while (!isNumeric(lexer.currentChar())) {
////                    if (!(lexer.currentChar() == '0' || lexer.currentChar() == '1')) {
////                        throw error("Binary literals can only have '1' and '0'.");
////                    }
////                    lexer.advance();
////                }
////
////                int value = Integer.parseInt(lexer.getStr().substring(_start, lexer.pos()), 2);
////                return new Int(value);
////            }
////            // HEXADECIMAL
////            else if (lexer.consume("0x")) {
////                int _start = lexer.pos();
////
////                while (isLiteral(lexer.currentChar())) {
////                    if (!(lexer.currentChar() >= '0' && lexer.currentChar() <= '9'
////                            || lexer.currentChar() >= 'A' && lexer.currentChar() <= 'F')) {
////                        throw error("Hexadecimal literals can only have '1-9' and 'A-F'.");
////                    }
////                    lexer.advance();
////                }
////
////                int value = Integer.parseInt(lexer.getStr().substring(_start, lexer.pos()), 16);
////                return Int.of(value);
////            } else
//            {
//                while (!lexer.peek("..") && isNumeric(lexer.currentChar())) lexer.advance();
//
//                String str = lexer.getStr().substring(start, lexer.pos()).trim();
//
//                if (lexer.consume('i')) {
//                    any = Complex.of(0, Double.parseDouble(str));
//                } else if (!str.contains(".")) {
//                    any = Int.of(Integer.parseInt(str));
//                } else {
//                    any = Decimal.of(Double.parseDouble(str));
//                }
//            }
//        }
//        else if (lexer.consume('i')) {
//            any = Complex.of(0, 1);
//        }
//        // LISTS
//        else if (lexer.consume('[')) {
//            if (lexer.consume(']')) return Slice.EMPTY;
//
//            Slice list = new Slice();
//
//            do {
//                Any value = compileExpression();
//                list.add(value);
//            }
//            while (lexer.consume(','));
//
//            if (!lexer.consume(']')) throw error("Expected list literal to close.");
//
//            any = list;
//        }
//
//        else if (lexer.consume("true")) {
//            any = Truth.TRUE;
//        } else if (lexer.consume("false")) {
//            any = Truth.FALSE;
//        }
//
//        // FUNCTION AND CONSTANTS
//        else if (Character.isLetter(lexer.currentChar())) {
//            String name = lexer.nextString();
//
//            any = pool.get(name);
//
//            if (any == null) {
//                throw error("Could not resolve reference `" + name + "`.");
//            }
//
//        } else {
//            throw error("Unexpected token: " + lexer.currentChar());
//        }
//
//        if (lexer.consume('(')) {
//            List<Any> args = new ArrayList<>();
//
//            if (!lexer.consume(')')) {
//
//                do {
//                    args.add(compileExpression());
//                } while (lexer.consume(','));
//
//                if (!lexer.consume(')')) {
//                    throw error("expected invocation to close");
//                }
//            }
//
//            any = any.invoke(args);
//        }
//
//        return any;
//    }

    private Any process(Any a, Any b, BinaryOperator<Any> function) {
        if (a.getType() == b.getType()) {
            return function.apply(a, b);
        }

        if (a instanceof Int) {
            if (b instanceof Decimal) {
                a = Decimal.of(((Int) a).value());
            } else if (b instanceof Complex) {
                a = Complex.of(((Int) a).value());
            }
        } else if (a instanceof Decimal) {
            if (b instanceof Int) {
                b = Decimal.of(((Int) b).value());
            } else if (b instanceof Complex) {
                a = Complex.of(((Decimal) a).value());
            }
        } else if (a instanceof Complex) {
            if (b instanceof Int) {
                b = Complex.of(((Int) b).value());
            } else if (b instanceof Decimal) {
                b = Complex.of(((Decimal) b).value());
            }
        }
        return function.apply(a, b);
    }

    private AJEException error(String message) {
        return new AJEException(message + " " + lexer.currentInfo());
    }
}
