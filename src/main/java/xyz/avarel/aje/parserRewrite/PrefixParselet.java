package xyz.avarel.aje.parserRewrite;

public interface PrefixParselet {
    Expression parse(Parser parser, Token token);
}