package xyz.avarel.aje.parserRewrite.parsers;

import xyz.avarel.aje.parserRewrite.AJEParser2;
import xyz.avarel.aje.parserRewrite.PrefixParselet;
import xyz.avarel.aje.parserRewrite.Token;
import xyz.avarel.aje.types.Any;

public class NameParser implements PrefixParselet {
    @Override
    public Any parse(AJEParser2 parser, Token token) {
        return parser.getObjects().get(token.getText());
    }
}
