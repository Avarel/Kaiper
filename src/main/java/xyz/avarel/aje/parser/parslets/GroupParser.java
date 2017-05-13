package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;

public class GroupParser implements PrefixParser<Any> {
    @Override
    public Any parse(AJEParser parser, Token token) {
        Any expression = parser.parse();
        parser.eat(TokenType.RIGHT_PAREN);
        return expression;
    }
}
