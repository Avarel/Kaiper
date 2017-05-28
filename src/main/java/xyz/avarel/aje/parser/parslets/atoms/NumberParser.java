package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Complex;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;

public class NumberParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        Expr value = null;

        if (parser.match(TokenType.IMAGINARY)) {
            String str = token.getText();
            value = new ValueAtom(token.getPosition(), Complex.of(0, Double.parseDouble(str)));
        } else if (token.getType() == TokenType.IMAGINARY) {
            value = new ValueAtom(token.getPosition(), Complex.of(0, 1));
        } else if (token.getType() == TokenType.INT) {
            String str = token.getText();
            value = new ValueAtom(token.getPosition(), Int.of(Integer.parseInt(str)));
        } else if (token.getType() == TokenType.DECIMAL) {
            String str = token.getText();
            value = new ValueAtom(token.getPosition(), Decimal.of(Double.parseDouble(str)));
        }

        if (parser.nextIs(TokenType.NAME)) {
            Expr right = parser.parseExpr(Precedence.MULTIPLICATIVE);
            return new BinaryOperation(right.getPosition(), value, right, Obj::times);
        }

        return value != null ? value : new ValueAtom(token.getPosition(), Undefined.VALUE);
    }
}
