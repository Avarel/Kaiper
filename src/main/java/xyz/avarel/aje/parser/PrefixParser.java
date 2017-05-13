package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.lexer.Token;

public interface PrefixParser<OUT> {
    OUT parse(AJEParser parser, Token token);
}