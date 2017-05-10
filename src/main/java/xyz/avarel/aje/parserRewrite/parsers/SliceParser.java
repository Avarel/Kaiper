package xyz.avarel.aje.parserRewrite.parsers;

import xyz.avarel.aje.parserRewrite.AJEParser2;
import xyz.avarel.aje.parserRewrite.PrefixParselet;
import xyz.avarel.aje.parserRewrite.Token;
import xyz.avarel.aje.parserRewrite.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.others.Slice;

public class SliceParser implements PrefixParselet {
    @Override
    public Any parse(AJEParser2 parser, Token token) {
        Slice slice = new Slice();

        if (!parser.match(TokenType.COMMA)) {
            do {
                slice.add(parser.parse());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return slice;
    }
}
