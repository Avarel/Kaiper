package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Slice;

import java.util.ArrayList;
import java.util.List;

public class GetIndexParser extends BinaryParser {
    public GetIndexParser(int precedence) {
        super(precedence, true);
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        if (!(left instanceof Slice)) throw new AJEException("can only use on slice");

        Int index = parser.parse(Int.TYPE);

        if (parser.match(TokenType.COMMA)) {
            List<Int> indices = new ArrayList<>();
            indices.add(index);
            do {
                indices.add(parser.parse(Int.TYPE));
            } while (parser.match(TokenType.COMMA));

            Slice slice = new Slice();
            for (Int i : indices) {
                slice.add(((Slice) left).get(i.value()));
            }
            return slice;
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return ((Slice) left).get(index.value());
    }
}
