package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.operations.GetOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class GetParser extends BinaryParser {
    public GetParser() {
        super(Precedence.ATTRIBUTE);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (parser.match(TokenType.COLON)) { // [:
            return parseEnd(parser, token.getPosition(), left, null);
        }

        Expr index = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseEnd(parser, token.getPosition(), left, index);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new GetOperation(token.getPosition(), left, index);
    }

    public Expr parseEnd(AJEParser parser, Position position, Expr left, Expr start) {
        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, position, left, start, null);
        } else if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(position, left, start, null, null);
        }

        Expr end = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, position, left, start, end);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(position, left, start, end, null);
    }

    public Expr parseStep(AJEParser parser, Position position, Expr left, Expr start, Expr end) {
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(position, left, start, end, null);
        }

        Expr step = parser.parseExpr();

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(position, left, start, end, step);
    }
}
