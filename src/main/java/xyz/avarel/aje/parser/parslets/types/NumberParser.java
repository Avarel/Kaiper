package xyz.avarel.aje.parser.parslets.types;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atom.UndefExpr;
import xyz.avarel.aje.parser.expr.atom.ValueExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.types.numbers.Complex;
import xyz.avarel.aje.runtime.types.numbers.Decimal;
import xyz.avarel.aje.runtime.types.numbers.Int;

public class NumberParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (parser.match(TokenType.IMAGINARY)) {
            String str = token.getText();
            return new ValueExpr(Complex.of(0, Double.parseDouble(str)));
        } else if (token.getType() == TokenType.IMAGINARY) {
            return new ValueExpr(Complex.of(0, 1));
        } else if (token.getType() == TokenType.INT) {
            String str = token.getText();
            return new ValueExpr(Int.of(Integer.parseInt(str)));
        } else if (token.getType() == TokenType.DECIMAL) {
            String str = token.getText();
            return new ValueExpr(Decimal.of(Double.parseDouble(str)));
        }
        return UndefExpr.VALUE;
    }
}
