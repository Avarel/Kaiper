package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Undefined;

public class NumberParser implements PrefixParser<Any> {
    @Override
    public Any parse(AJEParser parser, Token token) {
        if (parser.match(TokenType.IMAGINARY)) {
            String str = token.getText();
            return Complex.of(0, Double.parseDouble(str));
        } else if (token.getType() == TokenType.IMAGINARY) {
            return Complex.of(0, 1);
        } else if (token.getType() == TokenType.INT) {
            String str = token.getText();
            return Int.of(Integer.parseInt(str));
        } else if (token.getType() == TokenType.DECIMAL) {
            String str = token.getText();
            return Decimal.of(Double.parseDouble(str));
        }
        return Undefined.VALUE;
    }
}
