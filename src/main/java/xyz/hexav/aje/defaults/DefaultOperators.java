package xyz.hexav.aje.defaults;

import xyz.hexav.aje.expressions.VariableAssignment;
import xyz.hexav.aje.expressions.VariableExpression;
import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.types.Expression;
import xyz.hexav.aje.types.Slice;

public enum DefaultOperators {
    LOGICAL_OR(new Operator("||", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() != 0 || b.value() != 0 ? 1 : 0;
        }
    }),
    LOGICAL_AND(new Operator("&&", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() != 0 && b.value() != 0 ? 1 : 0;
        }
    }),

    ADD(new Operator("+", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() + b.value();
        }
    }),
    SUBTRACT(new Operator("-", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() - b.value();
        }
    }),

    MULTIPLY(new Operator("*", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() * b.value();
        }
    }),
    DIVIDE(new Operator("/", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() / b.value();
        }
    }),
    REMAINDER(new Operator("%", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() % b.value();
        }
    }),
    MODULUS(new Operator("mod", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return  () -> (a.value() % b.value() + b.value()) % b.value();
        }
    }),
    PERCENTAGE(new Operator("% of ", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return  () -> a.value() / 100 * b.value();
        }
    }),
    N_ROOT(new Operator("th root of ", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            double result = Math.pow(b.value(), 1.0 / a.value());

            if (Math.ceil(result) - result < 1E-10
                    || result - Math.floor(result) < 1E-10) {
                result = Math.round(result);
            }

            double finalResult = result;
            return () -> finalResult;
        }
    }),

    EQUALS(new Operator("==", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() == b.value() ? 1 : 0;
        }
    }),
    NOT_EQUALS(new Operator("!=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() != b.value() ? 1 : 0;
        }
    }),

    GREATER_OR_EQUAL(new Operator(">=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() >= b.value() ? 1 : 0;
        }
    }),
    GREATER_THAN(new Operator(">", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
             return () -> a.value() > b.value() ? 1 : 0;
        }
    }),
    LESSER_OR_EQUAL(new Operator("<=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() <= b.value() ? 1 : 0;
        }
    }),
    LESSER_THAN(new Operator("<", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() < b.value() ? 1 : 0;
        }
    }),

    ZERO_FILL_RIGHT_SHIFT(new Operator(">>>", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> (int) a.value() >>> (int) b.value();
        }
    }),
    RIGHT_SHIFT(new Operator(">>", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
             return () -> (int) a.value() >> (int) b.value();
        }
    }),
    LEFT_SHIFT(new Operator("<<", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> (int) a.value() << (int) b.value();
        }
    }),

    UNARY_PLUS(new Operator("+", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return a;
        }
    }),
    UNARY_MINUS(new Operator("-", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> -a.value();
        }
    }),
    BITWISE_COMPLEMENT(new Operator("~", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> ~(int) a.value();
        }
    }),
    LOGICAL_NOT(new Operator("!", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() == 0 ? 1 : 0;
        }
    }),

    EXPONENTATION(new Operator("^", 2, false) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> Math.pow(a.value(), b.value());
        }
    }),
    SCIENTIFIC_EX(new Operator("E", 2, false) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() * Math.pow(10, b.value());
        }
    }),

    DEGREES(new Operator("deg", 1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> Math.toRadians(a.value());
        }
    }),


    ITEM_AT_LIST(new Operator("@", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            if (a instanceof Slice) {
                Slice list = (Slice) a;
                return () -> (list.get((int) b.value())).value();
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
        public Expression apply(Expression a, Expression b) {
            if (!(a instanceof VariableExpression)) {
                throw new RuntimeException("Left of assignment operation must be a variable.");
            }

            return new VariableAssignment(((VariableExpression) a).getVariable(), b);
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
