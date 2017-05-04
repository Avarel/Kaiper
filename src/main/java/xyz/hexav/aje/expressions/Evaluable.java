package xyz.hexav.aje.expressions;

import xyz.hexav.aje.types.AJEValue;

@FunctionalInterface
public strictfp interface Evaluable {
    double evalArgs(AJEValue... args);
}
