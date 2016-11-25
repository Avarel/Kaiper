package xyz.hexav.aje;

import xyz.hexav.aje.expressions.Expression;

import java.util.List;

public interface Function extends Expression
{
    Function input(int index, double input);
    
    Function input(String param, double input);
    
    Function input(double... inputs);
    
    List<String> getParameters();
    
    String getName();
    
    int getParametersCount();
}
