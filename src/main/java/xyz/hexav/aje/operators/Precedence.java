package xyz.hexav.aje.operators;

public class Precedence {
    public static final int POSTFIX = 11;
    public static final int INFIX = 10;
    public static final int EXPONENTIAL = 9;
    public static final int UNARY = 8;
    public static final int MULTIPLICATIVE = 7;
    public static final int ADDITIVE = 6;
    public static final int SHIFT = 5;
    public static final int RELATIONAL = 5; // 4
    public static final int EQUALITY = 4; // 3
    public static final int LOGICAL_AND = 3; // 2
    public static final int LOGICAL_OR = 2; // 1
    public static final int ASSIGNMENT = 0;
}
