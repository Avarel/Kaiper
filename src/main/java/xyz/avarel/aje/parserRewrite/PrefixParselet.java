package xyz.avarel.aje.parserRewrite;

import xyz.avarel.aje.types.Any;

public interface PrefixParselet {
    Any parse(AJEParser2 parser, Token token);
}