package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Slice;
import xyz.avarel.aje.types.numbers.Int;

import java.util.ArrayList;
import java.util.List;

public class GetIndexParser extends BinaryParser<Slice, Any> {
    public GetIndexParser() {
        super(Precedence.ACCESS, true, false);
    }

    @Override
    public Any parse(AJEParser parser, Slice left, Token token) {
        Int index = parser.parse(Int.TYPE);

        if (parser.match(TokenType.COMMA)) {
            List<Int> indices = new ArrayList<>();
            indices.add(index);
            do {
                indices.add(parser.parse(Int.TYPE).identity());
            } while (parser.match(TokenType.COMMA));

            parser.eat(TokenType.RIGHT_BRACKET);

            Slice subslice = new Slice();
            for (Int i : indices) {
                subslice.add(left.get(i.value()));
            }

            return subslice;
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return left.get(index.value());
    }
}
