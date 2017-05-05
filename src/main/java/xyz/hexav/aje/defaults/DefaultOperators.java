package xyz.hexav.aje.defaults;

import xyz.hexav.aje.expressions.VariableAssignment;
import xyz.hexav.aje.expressions.VariableExpression;
import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.types.Expression;
import xyz.hexav.aje.types.Slice;

public enum DefaultOperators implements Operator {
    LOGICAL_OR("||", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() != 0 || b.value() != 0 ? 1 : 0;
        }
    },
    LOGICAL_AND("&&", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() != 0 && b.value() != 0 ? 1 : 0;
        }
    },

    ADD("+", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() + b.value();
        }
    },
    SUBTRACT("-", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() - b.value();
        }
    },

    MULTIPLY("*", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() * b.value();
        }
    },
    DIVIDE("/", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() / b.value();
        }
    },
    REMAINDER("%", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() % b.value();
        }
    },
    MODULUS("mod", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return  () -> (a.value() % b.value() + b.value()) % b.value();
        }
    },
    PERCENTAGE("% of ", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return  () -> a.value() / 100 * b.value();
        }
    },
    N_ROOT("th root of ", 2) {
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
    },

    EQUALS("==", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() == b.value() ? 1 : 0;
        }
    },
    NOT_EQUALS("!=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() != b.value() ? 1 : 0;
        }
    },

    GREATER_OR_EQUAL(">=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() >= b.value() ? 1 : 0;
        }
    },
    GREATER_THAN(">", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
             return () -> a.value() > b.value() ? 1 : 0;
        }
    },
    LESSER_OR_EQUAL("<=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() <= b.value() ? 1 : 0;
        }
    },
    LESSER_THAN("<", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() < b.value() ? 1 : 0;
        }
    },

    ZERO_FILL_RIGHT_SHIFT(">>>", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> (int) a.value() >>> (int) b.value();
        }
    },
    RIGHT_SHIFT(">>", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
             return () -> (int) a.value() >> (int) b.value();
        }
    },
    LEFT_SHIFT("<<", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> (int) a.value() << (int) b.value();
        }
    },

    UNARY_PLUS("+", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return a;
        }
    },
    UNARY_MINUS("-", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> -a.value();
        }
    },
    BITWISE_COMPLEMENT("~", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> ~(int) a.value();
        }
    },
    LOGICAL_NOT("!", -1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() == 0 ? 1 : 0;
        }
    },

    EXPONENTATION("^", 2, false) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> Math.pow(a.value(), b.value());
        }
    },
    SCIENTIFIC_EX("E", 2, false) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> a.value() * Math.pow(10, b.value());
        }
    },

    DEGREES("deg", 1) {
        @Override
        public Expression apply(Expression a, Expression b) {
            return () -> Math.toRadians(a.value());
        }
    },


    LIST_INDEX("@", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            if (a instanceof Slice) {
                Slice list = (Slice) a;
                if (b instanceof Slice) {
                    Slice indices = (Slice) b;
                    return list.subslice(indices);
                }
                return () -> { //FIXME ranges
                    return (list.get((int) b.value())).value();
                };
            }
            throw new RuntimeException("Attempted to use get at `@` operator on a non-list.");
        }
    },

//    POST_INCREMENT("++", 1) {
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
//    POST_DECREMENT("--", 1) {
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
//    PRE_INCREMENT("++", -1) {
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
//    PRE_DECREMENT("--", -1) {
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
    VAR_ASSIGNMENT("=", 2) {
        @Override
        public Expression apply(Expression a, Expression b) {
            if (!(a instanceof VariableExpression)) {
                throw new RuntimeException("Left of assignment operation must be a variable.");
            }

            return new VariableAssignment(((VariableExpression) a).getVariable(), b);
        }
    },
    ;


    private final String symbol;
    private final int args;
    private final boolean leftAssoc;

    DefaultOperators(String symbol, int args) {
        this(symbol, args, true);
    }

    DefaultOperators(String symbol, int args, boolean leftAssoc) {
        this.symbol = symbol;
        this.args = args;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getArgs() {
        return args;
    }

    @Override
    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}
