package xyz.avarel.aje.parserRewrite;

import xyz.avarel.aje.types.Any;

public interface InfixParser {
    Any parse(AJEParser2 parser, Any left, Token token);
    int getPrecedence();
}