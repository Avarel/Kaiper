package xyz.avarel.aje.parser.parslets.types;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.UndefExpr;
import xyz.avarel.aje.parser.lexer.Token;

public class SliceParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
//        Slice slice = new Slice();
//
//        if (!parser.match(TokenType.RIGHT_BRACKET)) {
//            do {
//                Any<Any> item = parser.parse();
//
//                if (parser.match(TokenType.RANGE_TO)) {
//                    Any end = parser.parse(item.getType());
//                    slice.addAll(item.rangeTo(end));
//                    continue;
//                }
//
//                slice.add(item);
//            } while (parser.match(TokenType.COMMA));
//            parser.eat(TokenType.RIGHT_BRACKET);
//        }
//
//        return slice;
        return UndefExpr.VALUE;
    }
}
