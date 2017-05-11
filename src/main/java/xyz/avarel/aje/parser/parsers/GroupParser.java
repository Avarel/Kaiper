package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.Token;
import xyz.avarel.aje.parser.TokenType;
import xyz.avarel.aje.types.Any;

public class GroupParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {
        Any expression = parser.parse();
        parser.eat(TokenType.RIGHT_PAREN);
        return expression;
    }
}
