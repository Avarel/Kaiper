package xyz.hexavalon.aje.expressions;

@FunctionalInterface
public interface Evaluable
{
    double eval(double... args);
}
