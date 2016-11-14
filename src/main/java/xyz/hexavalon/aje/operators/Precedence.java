package xyz.hexavalon.aje.operators;

public class Precedence
{
    public static final int POSTFIX = 1100;
    public static final int INFIX = 1000;
    public static final int EXPONENTIAL = 900;
    public static final int UNARY = 800;
    public static final int MULTIPLICATIVE = 700;
    public static final int ADDITIVE = 600;
    public static final int SHIFT = 500;
    public static final int RELATIONAL = 400;
    public static final int EQUALITY = 300;
    public static final int LOGICAL_AND = 200;
    public static final int LOGICAL_OR = 100;
    public static final int ASSIGNMENT = 0;
}
