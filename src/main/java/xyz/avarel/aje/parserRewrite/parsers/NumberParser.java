package xyz.avarel.aje.parserRewrite.parsers;

import xyz.avarel.aje.parserRewrite.AJEParser2;
import xyz.avarel.aje.parserRewrite.PrefixParselet;
import xyz.avarel.aje.parserRewrite.Token;
import xyz.avarel.aje.parserRewrite.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.others.Undefined;

public class NumberParser implements PrefixParselet {
    @Override
    public Any parse(AJEParser2 parser, Token token) {
        if (token.getType() == TokenType.NUMERIC) {
            return Decimal.of(Double.parseDouble(token.getText()));
        }
        return Undefined.VALUE;
    }
}
