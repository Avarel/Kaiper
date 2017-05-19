package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.operations.SliceExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.ArrayList;
import java.util.List;

public class SliceParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {
        List<Expr> exprs = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_BRACKET)) {
            do {
                parser.match(TokenType.LINE);
                exprs.add(parser.parseExpr(pool));
            } while (parser.match(TokenType.COMMA));
            parser.match(TokenType.LINE);
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return new SliceExpr(exprs);
    }
}
