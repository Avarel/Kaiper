package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.Token;
import xyz.avarel.aje.parser.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;

public class NumberParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {
        String str = token.getText();
        if (parser.match(TokenType.BACKSLASH)) {
            return Complex.of(0, Double.parseDouble(str));
        } else if (!str.contains(".")) {
            return Int.of(Integer.parseInt(str));
        } else {
            return Decimal.of(Double.parseDouble(str));
        }
    }
}
