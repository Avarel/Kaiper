package xyz.avarel.aje.parser.parslets.types;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.operations.SliceExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class SliceParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Expr> exprs = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_BRACKET)) {
            do {
                exprs.add(parser.parse());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return new SliceExpr(exprs);
    }
}
