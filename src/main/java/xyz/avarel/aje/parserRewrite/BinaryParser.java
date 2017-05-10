package xyz.avarel.aje.parserRewrite;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.others.Undefined;

public class BinaryParser implements InfixParser {
    private final int precedence;
    private final boolean leftAssoc;

    public BinaryParser(int precedence, boolean leftAssoc) {
        this.precedence = precedence;
        this.leftAssoc = leftAssoc;
    }

    public Any parse(AJEParser2 parser, Any left, Token token) {
        Any right = parser.parse(precedence - (leftAssoc ? 0 : 1));
        System.out.println(token);

        switch(token.getType()) {
            case PLUS: return left.plus(right);
            case MINUS: return left.minus(right);
            case ASTERISK: return left.times(right);
            default: return Undefined.VALUE;
        }
    }

    public int getPrecedence() {
        return precedence;
    }
}