package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.operations.GetOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class GetParser extends BinaryParser {
    public GetParser() {
        super(Precedence.ACCESS);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (parser.match(TokenType.COLON)) { // [:
            return parseEnd(parser, left, null);
        }

        Expr index = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseEnd(parser, left, index);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new GetOperation(left, index);
    }

    public Expr parseEnd(AJEParser parser, Expr left, Expr start) {
        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, left, start, null);
        } else if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(left, start, null, null);
        }

        Expr end = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, left, start, end);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(left, start, end, null);
    }

    public Expr parseStep(AJEParser parser, Expr left, Expr start, Expr end) {
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(left, start, end, null);
        }

        Expr step = parser.parseExpr();

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(left, start, end, step);
    }
}
