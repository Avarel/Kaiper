package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.others.Slice;

public class SliceParser implements PrefixParser<Slice> {
    @Override
    public Slice parse(AJEParser parser, Token token) {
        Slice slice = new Slice();

        if (!parser.match(TokenType.RIGHT_BRACKET)) {
            do {
                Any<Any> item = parser.parse();

                if (parser.match(TokenType.RANGE_TO)) {
                    Any end = parser.parse(item.getType());
                    slice.addAll(item.rangeTo(end));
                    continue;
                }

                slice.add(item);
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return slice;
    }
}
