package xyz.avarel.aje.parser.parslets.operator;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.RangeExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class RangeOperatorParser extends BinaryParser {
    public RangeOperatorParser() {
        super(Precedence.RANGE_TO);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        boolean exclusive = false;
        if (parser.match(TokenType.LT)) {
            exclusive = true;
        }

        Expr right = parser.parseExpr(getPrecedence());
        return new RangeExpr(token.getPosition(), left, right, exclusive);
    }
}