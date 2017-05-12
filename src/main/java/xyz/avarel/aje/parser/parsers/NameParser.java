package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.others.Undefined;

public class NameParser implements PrefixParser<Any> {
    @Override
    public Any parse(AJEParser parser, Token token) {
        Any obj = parser.getObjects().get(token.getText());
        return obj == null ? Undefined.VALUE : obj;
    }
}
