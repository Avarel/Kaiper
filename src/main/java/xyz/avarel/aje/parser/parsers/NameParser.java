package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.Token;
import xyz.avarel.aje.types.Any;

public class NameParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {
        return parser.getObjects().get(token.getText());
    }
}
