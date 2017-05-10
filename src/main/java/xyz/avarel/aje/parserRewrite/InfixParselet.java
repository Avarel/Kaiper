package xyz.avarel.aje.parserRewrite;

public interface InfixParselet {
    Expression parse(Parser parser, Expression left, Token token);
    int getPrecedence();
}