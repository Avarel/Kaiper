package xyz.hexav.aje;

import xyz.hexav.aje.types.AJEValue;

import java.util.List;

public interface Function extends AJEValue {
    Function input(int index, double input);

    Function input(String param, double input);

    Function input(double... inputs);

    // TODO implement varargs

    List<String> getParameters();

    String getName();

    int getParametersCount();
}
