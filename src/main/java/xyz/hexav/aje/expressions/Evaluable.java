package xyz.hexav.aje.expressions;

@FunctionalInterface
public interface Evaluable
{
    double eval(double... args);
}
