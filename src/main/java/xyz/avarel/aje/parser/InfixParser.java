package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.lexer.Token;

public interface InfixParser<IN, OUT> {
    OUT parse(AJEParser parser, IN left, Token token);
    int getPrecedence();
    default boolean keepIdentity() {
        return false;
    }
}