package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Slice;

public class GetIndexParser extends BinaryParser {
    public GetIndexParser(int precedence) {
        super(precedence, true);
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        if (!(left instanceof Slice)) throw new AJEException("can only use on slice");

        Any any = parser.parse();

        parser.eat(TokenType.RIGHT_BRACKET);

        if (any.getType() != Int.TYPE) throw new AJEException("index must be int");

        return ((Slice) left).get(((Int) any).value());
    }
}
