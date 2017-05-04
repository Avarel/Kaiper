package xyz.hexav.aje.defaults;

import xyz.hexav.aje.expressions.VariableAssignment;
import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.operators.Precedence;
import xyz.hexav.aje.pool.Variable;
import xyz.hexav.aje.types.AJEList;
import xyz.hexav.aje.types.AJEValue;

public enum DefaultOperators {
    LOGICAL_OR(new Operator("||", 2, args -> args[0].value() != 0 || args[1].value() != 0 ? 1 : 0)),
    LOGICAL_AND(new Operator("&&", 2, args -> args[0].value() != 0 && args[1].value() != 0 ? 1 : 0)),

    ADD(new Operator("+", 2, args -> args[0].value() + args[1].value())),
    SUBTRACT(new Operator("-", 2, args -> args[0].value() - args[1].value())),

    MULTIPLY(new Operator("*", 2, args -> args[0].value() * args[1].value())),
    DIVIDE(new Operator("/", 2, args -> args[0].value() / args[1].value())),
    REMAINDER(new Operator("%", 2, args -> args[0].value() % args[1].value())),
    MODULUS(new Operator("mod", 2, args -> (args[0].value() % args[1].value() + args[1].value()) % args[1].value())),
    PERCENTAGE(new Operator("% of ", 2, args -> args[0].value() / 100 * args[1].value())),
    N_ROOT(new Operator("th root of ", 2, args -> {
        double result = Math.pow(args[1].value(), 1.0 / args[0].value());

        if (Math.ceil(result) - result < 1E-10
                || result - Math.floor(result) < 1E-10) {
            result = Math.round(result);
        }

        return result;
    })),

    EQUALS(new Operator("==", 2, args -> args[0].value() == args[1].value() ? 1 : 0)),
    NOT_EQUALS(new Operator("!=", 2, args -> args[0].value() != args[1].value() ? 1 : 0)),

    GREATER_OR_EQUAL(new Operator(">=", 2, args -> args[0].value() >= args[1].value() ? 1 : 0)),
    GREATER_THAN(new Operator(">", 2, args ->  args[0].value() > args[1].value() ? 1 : 0)),
    LESSER_OR_EQUAL(new Operator("<=", 2, args -> args[0].value() <= args[1].value() ? 1 : 0)),
    LESSER_THAN(new Operator("<", 2, args -> args[0].value() < args[1].value() ? 1 : 0)),

    ZERO_FILL_RIGHT_SHIFT(new Operator(">>>", 2, args -> (int) args[0].value() >>> (int) args[1].value())),
    RIGHT_SHIFT(new Operator(">>", 2, args ->  (int) args[0].value() >> (int) args[1].value())),
    LEFT_SHIFT(new Operator("<<", 2, args -> (int) args[0].value() << (int) args[1].value())),

    UNARY_PLUS(new Operator("+", -1, args -> +args[0].value())),
    UNARY_MINUS(new Operator("-", -1, args -> -args[0].value())),
    BITWISE_COMPLEMENT(new Operator("~", -1, args -> ~(int) args[0].value())),
    LOGICAL_NOT(new Operator("!", -1, args -> args[0].value() == 0 ? 1 : 0)),

    EXPONENTATION(new Operator("^", 2, Precedence.UNARY, args -> Math.pow(args[0].value(), args[1].value()))),
    SCIENTIFIC_EX(new Operator("E", 2, Precedence.UNARY, args -> args[0].value() * Math.pow(10, args[1].value()))),

    DEGREES(new Operator("deg", 1, args -> Math.toRadians(args[0].value()))),


    ITEM_AT_LIST(new Operator("@", 2) {
        @Override
        public AJEValue compile(AJEValue a, AJEValue b) {
            if (a instanceof AJEList) {
                AJEList list = (AJEList) a;
                return list.get((int) b.value());
            }
            throw new RuntimeException("Attempted to use get at `@` operator on a non-list.");
        }
    }),

//    POST_INCREMENT(new Operator("++", 1) {
//        @Override
//        public AJENumber compile(AJENumber a) {
//            if (!(a instanceof VariableAJENumber)) {
//                throw new RuntimeException("Left of assignment operation must be a variable AJENumber.");
//            }
//
//            VariableAJENumber _a = (VariableAJENumber) a;
//
//            return new VariableAssignment(_a.getVariable(), new double[]{_a.eval() + 1}) {
//                @Override
//                public double[] evalList() {
//                    double[] result = getVariable().eval();
//                    if (getAJENumber() != null) getVariable().assign(getAJENumber().evalList());
//                    return result;
//                }
//            };
//        }
//    }),
//    POST_DECREMENT(new Operator("--", 1) {
//        @Override
//        public AJENumber compile(AJENumber a) {
//            if (!(a instanceof VariableAJENumber)) {
//                throw new RuntimeException("Left of assignment operation must be a variable.");
//            }
//
//            VariableAJENumber _a = (VariableAJENumber) a;
//
//            return new VariableAssignment(_a.getVariable(), new double[]{_a.eval() - 1}) {
//                @Override
//                public double[] evalList() {
//                    double[] result = getVariable().eval();
//                    if (getAJENumber() != null) getVariable().assign(getAJENumber().evalList());
//                    return result;
//                }
//            };
//        }
//    }),
//
//    PRE_INCREMENT(new Operator("++", -1) {
//        @Override
//        public AJENumber compile(AJENumber a) {
//            if (!(a instanceof VariableAJENumber)) {
//                throw new RuntimeException("Left of assignment operation must be a variable AJENumber.");
//            }
//
//            VariableAJENumber _a = (VariableAJENumber) a;
//
//            return new VariableAssignment(_a.getVariable(), new double[]{_a.eval() + 1});
//        }
//    }),
//    PRE_DECREMENT(new Operator("--", -1) {
//        @Override
//        public AJENumber compile(AJENumber a) {
//            if (!(a instanceof VariableAJENumber)) {
//                throw new RuntimeException("Left of assignment operation must be a variable.");
//            }
//
//            VariableAJENumber _a = (VariableAJENumber) a;
//
//            return new VariableAssignment(_a.getVariable(), new double[]{_a.eval() - 1});
//        }
//    }),
//
    VAR_ASSIGNMENT(new Operator("=", 2) {
        @Override
        public AJEValue compile(AJEValue a, AJEValue b) {
            if (!(a instanceof Variable)) {
                throw new RuntimeException("Left of assignment operation must be a variable.");
            }

            Variable _a = (Variable) a;

            return new VariableAssignment(_a, b);
        }
    }),
    ;

    private final Operator operator;

    DefaultOperators(Operator operator) {
        this.operator = operator;
    }

    public Operator get() {
        return operator;
    }
}
