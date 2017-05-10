package xyz.avarel.aje.parserRewrite.parsers;

import xyz.avarel.aje.parserRewrite.AJEParser2;
import xyz.avarel.aje.parserRewrite.BinaryParser;
import xyz.avarel.aje.parserRewrite.Token;
import xyz.avarel.aje.parserRewrite.TokenType;
import xyz.avarel.aje.types.Any;

public class GetParser extends BinaryParser {
    public GetParser(int precedence) {
        super(precedence, true);
    }

    @Override
    public Any parse(AJEParser2 parser, Any left, Token token) {
        Token ntoken = parser.eat(TokenType.NAME);

        return left.get(ntoken.getText());
    }
}
