package xyz.avarel.aje.operators;

public class Precedence {
    public static final int POSTFIX = 11;
    public static final int PREFIX = 10;
    public static final int EXPONENTIAL = 9;
    public static final int MULTIPLICATIVE = 8;
    public static final int ADDITIVE = 7;
    public static final int RANGE_TO = 6; // 1
    public static final int INFIX = 5;
    public static final int COMPARISON = 4; // 4
    public static final int EQUALITY = 3; // 3
    public static final int CONJUNCTION = 2; // 2
    public static final int DISJUNCTION = 1; // 1
    public static final int ASSIGNMENT = 0;
}
