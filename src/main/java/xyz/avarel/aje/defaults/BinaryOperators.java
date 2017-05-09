package xyz.avarel.aje.defaults;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.operators.AJEBinaryOperator;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

@SuppressWarnings("unchecked")
public enum BinaryOperators implements AJEBinaryOperator {
    RANGE_TO("..") {
        @Override
        public Any apply(Any a, Any b) {
            return a.rangeTo(b);
        }
    },

    LOGICAL_OR("||") {
        @Override
        public Any apply(Any a, Any b) {
            Truth.assertIs(a, b);
            return a.plus(b);
        }
    },
    LOGICAL_AND("&&") {
        @Override
        public Any apply(Any a, Any b) {
            Truth.assertIs(a, b);
            return a.times(b);
        }
    },

    ADD("+") {
        @Override
        public Any apply(Any a, Any b) {
            return a.plus(b);
        }
    },
    SUBTRACT("-") {
        @Override
        public Any apply(Any a, Any b) {
            return a.minus(b);
        }
    },

    MULTIPLY("*") {
        @Override
        public Any apply(Any a, Any b) {
            return a.times(b);
        }
    },
    DIVIDE("/") {
        @Override
        public Any apply(Any a, Any b) {
            return a.divide(b);
        }
    },
    REMAINDER("%") {
        @Override
        public Any apply(Any a, Any b) {
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
        public Any compile(Any a, Any b) {
            return apply(a.castUp(Decimal.TYPE), b.castUp(Decimal.TYPE));
        }
        @Override
        public Any apply(Any a, Any b) {
            return a.divide(100).times(b);
        }
    },
    NTH_ROOT("th root of ") {
        @Override
        public Any compile(Any a, Any b) {
            if (b instanceof Int) {
                return apply(b.castUp(Decimal.TYPE), a.castUp(Decimal.TYPE));
            } else if (b instanceof Complex) {
                return apply(b, a);
            }
            return super.compile(a, b);
        }
        @Override
        public Any apply(Any a, Any b) {
            return b.root(a);
        }
    },

    EQUALS("==") {
        public Any apply(Any a, Any b) {
            return a.equals(b);
        }
    },
    NOT_EQUALS("!=") {
        public Any apply(Any a, Any b) {
            return a.equals(b).negative();
        }
    },

    GREATER_OR_EQUAL(">=") {
        public Any apply(Any a, Any b) {
            return a.greaterThanOrEqual(b);
        }
    },
    GREATER_THAN(">") {
        public Any apply(Any a, Any b) {
            return a.greaterThan(b);
        }
    },
    LESSER_OR_EQUAL("<=") {
        public Any apply(Any a, Any b) {
            return a.lessThanOrEqual(b);
        }
    },
    LESSER_THAN("<") {
        public Any apply(Any a, Any b) {
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
        public Any apply(Any a, Any b) {
            return a.pow(b);
        }
    },
    SCIENTIFIC_EX("E", false) {
        public Any apply(Any a, Any b) {
            Decimal.assertIs(b);
            return a.times(new Decimal(10).pow((Decimal) b));
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
    CAST("as ") {
        @Override
        public Any compile(Any any, Any any2) {
            if (!(any2 instanceof Type)) {
                throw new AJEException("Second argument of cast must be an AJE type!");
            }
            Type type = any.getType();
            for (Type implicits : type.getImplicitTypes()) {
                if (implicits == any2) {
                    return any.castUp(type);
                }
            }
            return any.castDown((Type) any2);
        }

    @Override
    public Any apply(Any any, Any any2) {
        return null;
    }
},

    LIST_INDEX("@") {
        @Override
        public Any compile(Any a, Any b) {
            if (!(b instanceof Int || b instanceof Slice)) {
                throw new AJEException(a.getType() + " get operations must take in an int or slice range.");
            }
            return apply(a, b);
        }
        @Override
        public Any apply(Any a, Any b) {
            Slice.assertIs(a);
            if (b instanceof Int) {
                return ((Slice) a).get(((Int) b).value());
            } else if (b instanceof Slice) {
                return ((Slice) a).get(((Slice) b));
            } else throw new AJEException("what");
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
