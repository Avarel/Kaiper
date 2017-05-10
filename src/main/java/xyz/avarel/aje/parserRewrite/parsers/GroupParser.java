package xyz.avarel.aje.parserRewrite.parsers;

import xyz.avarel.aje.parserRewrite.AJEParser2;
import xyz.avarel.aje.parserRewrite.PrefixParselet;
import xyz.avarel.aje.parserRewrite.Token;
import xyz.avarel.aje.parserRewrite.TokenType;
import xyz.avarel.aje.types.Any;

public class GroupParser implements PrefixParselet {
    @Override
    public Any parse(AJEParser2 parser, Token token) {
        Any expression = parser.parse();
        parser.eat(TokenType.RIGHT_PAREN);
        return expression;
    }
}
