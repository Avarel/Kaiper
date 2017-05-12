package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.parsers.AJEParser;

public interface InfixParser<IN, OUT> {
    OUT parse(AJEParser parser, IN left, Token token);
    int getPrecedence();
}