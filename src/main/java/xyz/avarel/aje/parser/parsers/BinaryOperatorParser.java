package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;

import java.util.function.BinaryOperator;

public class BinaryOperatorParser extends BinaryParser {
    private final BinaryOperator<Any> operator;

    public BinaryOperatorParser(int precedence, boolean leftAssoc, BinaryOperator<Any> operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Any parse(AJEParser parser, Any left, Token token) {
        Any right = parser.parse(getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return process(left, right, operator);
    }

    private Any process(Any a, Any b, BinaryOperator<Any> function) {
        if (a.getType() == b.getType()) {
            return function.apply(a, b);
        }

        if (a instanceof Int) {
            if (b instanceof Decimal) {
                a = Decimal.of(((Int) a).value());
            } else if (b instanceof Complex) {
                a = Complex.of(((Int) a).value());
            }
        } else if (a instanceof Decimal) {
            if (b instanceof Int) {
                b = Decimal.of(((Int) b).value());
            } else if (b instanceof Complex) {
                a = Complex.of(((Decimal) a).value());
            }
        } else if (a instanceof Complex) {
            if (b instanceof Int) {
                b = Complex.of(((Int) b).value());
            } else if (b instanceof Decimal) {
                b = Complex.of(((Decimal) b).value());
            }
        }

        return function.apply(a, b);
    }
}
