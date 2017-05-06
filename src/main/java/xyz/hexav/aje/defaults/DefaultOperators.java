package xyz.hexav.aje.defaults;

import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.types.ComparableValue;
import xyz.hexav.aje.types.OperableValue;
import xyz.hexav.aje.types.Truth;

public enum DefaultOperators implements Operator {
    LOGICAL_OR("||", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            Truth.check(a, b);
            return a.add(b);
        }
    },
    LOGICAL_AND("&&", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            Truth.check(a, b);
            return a.multiply(b);
        }
    },

    ADD("+", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.add(b);
        }
    },
    SUBTRACT("-", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.subtract(b);
        }
    },

    MULTIPLY("*", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.multiply(b);
        }
    },
    DIVIDE("/", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.divide(b);
        }
    },
    REMAINDER("%", 2) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.mod(b);
        }
    },
//    MODULUS("mod", 2) {
//        @Override
//        public OperableValue apply(OperableValue a, OperableValue b) {
//            return a.mod(b);
//        }
//    },

//    PERCENTAGE("% of ", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return  () -> a.value() / 100 * b.value();
//        }
//    },
//    N_ROOT("th root of ", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//            double result = Math.pow(b.value(), 1.0 / a.value());
//
//            if (Math.ceil(result) - result < 1E-10
//                    || result - Math.floor(result) < 1E-10) {
//                result = Math.round(result);
//            }
//
//            double finalResult = result;
//            return () -> finalResult;
//        }
//    },

    EQUALS("==", 2) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.equals(b);
        }
    },
    NOT_EQUALS("!=", 2) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.equals(b).negative();
        }
    },

    GREATER_OR_EQUAL(">=", 2) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            ComparableValue.check(a, b);
            return ComparableValue.wrap(a).greaterThanOrEqual(b);
        }
    },
    GREATER_THAN(">", 2) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            ComparableValue.check(a, b);
            return ComparableValue.wrap(a).greaterThan(b);
        }
    },
    LESSER_OR_EQUAL("<=", 2) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            ComparableValue.check(a, b);
            return ComparableValue.wrap(a).lessThanOrEqual(b);
        }
    },
    LESSER_THAN("<", 2) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            ComparableValue.check(a, b);
            return ComparableValue.wrap(a).lessThan(b);
        }
    },
//
//    ZERO_FILL_RIGHT_SHIFT(">>>", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> (int) a.value() >>> (int) b.value();
//        }
//    },
//    RIGHT_SHIFT(">>", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//             return () -> (int) a.value() >> (int) b.value();
//        }
//    },
//    LEFT_SHIFT("<<", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> (int) a.value() << (int) b.value();
//        }
//    },
//
    UNARY_PLUS("+", -1) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a;
        }
    },
    UNARY_MINUS("-", -1) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.negative();
        }
    },
//    BITWISE_COMPLEMENT("~", -1) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> ~(int) a.value();
//        }
//    },
//    LOGICAL_NOT("!", -1) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> a.value() == 0 ? 1 : 0;
//        }
//    },
//
//    EXPONENTATION("^", 2, false) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> Math.pow(a.value(), b.value());
//        }
//    },
//    SCIENTIFIC_EX("E", 2, false) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> a.value() * Math.pow(10, b.value());
//        }
//    },
//
//    DEGREES("deg", 1) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> Math.toRadians(a.value());
//        }
//    },
//
//
//    LIST_INDEX("@", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//            if (a instanceof Slice) {
//                Slice list = (Slice) a;
//                if (b instanceof Slice) {
//                    Slice indices = (Slice) b;
//                    return list.subList(indices);
//                }
//                return list.get(b);
//            }
//            throw new RuntimeException("Attempted to use get at `@` operator on a non-list.");
//        }
//    },

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
//    VAR_ASSIGNMENT("=", 2) {
//        @Override
//        public Value apply(Value a, Value b) {
//            if (!(a instanceof Variable)) {
//                throw new RuntimeException("Left of assignment operation must be a variable.");
//            }
//            return new VariableAssignment((Variable) a, b);
//        }
//    },
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
