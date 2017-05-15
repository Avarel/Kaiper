package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Truth;
import xyz.avarel.aje.runtime.types.Undefined;

public class BooleanParser implements PrefixParser<Any> {
    @Override
    public Any parse(AJEParser parser, Token token) {
        switch(token.getText()) {
            case "true": return Truth.TRUE;
            case "false": return Truth.FALSE;
            default: return Undefined.VALUE;
        }
    }
}
