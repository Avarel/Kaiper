package xyz.hexav.aje.defaults;

import xyz.hexav.aje.operators.AJEBinaryOperator;
import xyz.hexav.aje.types.Numeric;
import xyz.hexav.aje.types.interfaces.OperableValue;
import xyz.hexav.aje.types.Truth;

@SuppressWarnings("unchecked")
public enum BinaryOperators implements AJEBinaryOperator {
    LOGICAL_OR("||") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            Truth.assertIs(a, b);
            return a.add(b);
        }
    },
    LOGICAL_AND("&&") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            Truth.assertIs(a, b);
            return a.multiply(b);
        }
    },

    ADD("+") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.add(b);
        }
    },
    SUBTRACT("-") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.subtract(b);
        }
    },

    MULTIPLY("*") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.multiply(b);
        }
    },
    DIVIDE("/") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.divide(b);
        }
    },
    REMAINDER("%") {
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

    PERCENTAGE("% of ") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.divide(new Numeric(100)).multiply(b);
        }
    },
    NTH_ROOT("th root of ") {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.root(b);
        }
    },

    EQUALS("==") {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.equals(b);
        }
    },
    NOT_EQUALS("!=") {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.equals(b).negative();
        }
    },

    GREATER_OR_EQUAL(">=") {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.greaterThanOrEqual(b);
        }
    },
    GREATER_THAN(">") {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.greaterThan(b);
        }
    },
    LESSER_OR_EQUAL("<=") {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.lessThanOrEqual(b);
        }
    },
    LESSER_THAN("<") {
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.lessThan(b);
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
//    BITWISE_COMPLEMENT("~", -1) {
//        @Override
//        public Value apply(Value a, Value b) {
//            return () -> ~(int) a.value();
//        }
//    },
//
    EXPONENTATION("^", false) {
        @Override
        public OperableValue apply(OperableValue a, OperableValue b) {
            return a.pow(b);
        }
    },
    SCIENTIFIC_EX("E", false) {
        public OperableValue apply(OperableValue a, OperableValue b) {
            Numeric.assertIs(b);
            return a.multiply(new Numeric(10).pow((Numeric) b));
        }
    },
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
    private final boolean leftAssoc;

    BinaryOperators(String symbol) {
        this(symbol, true);
    }

    BinaryOperators(String symbol, boolean leftAssoc) {
        this.symbol = symbol;
        this.leftAssoc = leftAssoc;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean isLeftAssoc() {
        return leftAssoc;
    }
}
