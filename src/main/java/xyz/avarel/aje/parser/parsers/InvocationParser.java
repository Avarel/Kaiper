package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;

import java.util.ArrayList;
import java.util.List;

public class InvocationParser extends BinaryParser {
    public InvocationParser(int precedence) {
        super(precedence, true);
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        List<Any> list = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                list.add(parser.parse());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_PAREN);
        }

        return left.invoke(list);
    }
}
