package xyz.avarel.aje;

public class Precedence {
    public static final int ACCESS = 14;
    public static final int PIPE_FORWARD = 13;
    public static final int POSTFIX = 12;
    public static final int EXPONENTIAL = 11;
    public static final int PREFIX = 10;
    public static final int MULTIPLICATIVE = 9;
    public static final int ADDITIVE = 8;
    public static final int RANGE_TO = 7; // 1
    public static final int INFIX = 6;
    public static final int COMPARISON = 5; // 4
    public static final int EQUALITY = 4; // 3
    public static final int CONJUNCTION = 3; // 2
    public static final int DISJUNCTION = 2; // 1
    public static final int ASSIGNMENT = 1;
}
