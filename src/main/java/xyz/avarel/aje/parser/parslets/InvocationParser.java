package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.types.Any;

import java.util.ArrayList;
import java.util.List;

public class InvocationParser extends BinaryParser<Any, Any> {
    public InvocationParser() {
        super(Precedence.ACCESS, true);
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        List<Any> list = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                list.add(parser.parse().identity());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_PAREN);
        }

        return left.invoke(list);
    }
}
