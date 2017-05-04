package xyz.hexav.aje.operators;

public class Precedence {
    public static final int POSTFIX = 110;
    public static final int INFIX = 100;
    public static final int EXPONENTIAL = 90;
    public static final int UNARY = 80;
    public static final int MULTIPLICATIVE = 70;
    public static final int ADDITIVE = 60;
    public static final int SHIFT = 50;
    public static final int RELATIONAL = 40;
    public static final int EQUALITY = 30;
    public static final int LOGICAL_AND = 20;
    public static final int LOGICAL_OR = 10;
    public static final int ASSIGNMENT = 0;
}
