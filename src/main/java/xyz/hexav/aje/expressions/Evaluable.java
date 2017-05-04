package xyz.hexav.aje.expressions;

import xyz.hexav.aje.types.Expression;

@FunctionalInterface
public strictfp interface Evaluable {
    double evalArgs(Expression... args);
}
