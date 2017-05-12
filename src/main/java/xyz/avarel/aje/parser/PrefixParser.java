package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.parsers.AJEParser;
import xyz.avarel.aje.types.Any;

public interface PrefixParser {
    Any parse(AJEParser parser, Token token);
}