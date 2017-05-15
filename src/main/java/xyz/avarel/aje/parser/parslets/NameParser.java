package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.types.Any;

public class NameParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {
        return parser.getObjects().get(token.getText());
    }
}
