package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.Token;
import xyz.avarel.aje.parser.TokenType;
import xyz.avarel.aje.types.Any;

public class GetParser extends BinaryParser {
    public GetParser(int precedence) {
        super(precedence, true);
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        Token ntoken = parser.eat(TokenType.NAME);

        return left.get(ntoken.getText());
    }
}