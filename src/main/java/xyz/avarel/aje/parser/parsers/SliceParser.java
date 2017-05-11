package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.Token;
import xyz.avarel.aje.parser.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.others.Slice;

public class SliceParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {
        Slice slice = new Slice();

        if (!parser.match(TokenType.RIGHT_BRACKET)) {
            do {
                slice.add(parser.parse());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return slice;
    }
}
