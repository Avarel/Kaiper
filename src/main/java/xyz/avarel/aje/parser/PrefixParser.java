package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.parsers.AJEParser;

public interface PrefixParser<OUT> {
    OUT parse(AJEParser parser, Token token);
}