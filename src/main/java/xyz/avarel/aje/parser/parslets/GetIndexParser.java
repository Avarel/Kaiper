package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.UndefExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.types.Slice;

public class GetIndexParser extends BinaryParser<Slice, Expr> {
    public GetIndexParser() {
        super(Precedence.ACCESS, true, false);
    }

    @Override
    public Expr parse(AJEParser parser, Slice left, Token token) {
//        Int index = (Int) parser.parse();
//
//        if (parser.match(TokenType.COMMA)) {
//            List<Int> indices = new ArrayList<>();
//            indices.add(index);
//            do {
//                indices.add(Int.of(0)); //(Int) parser.parse().identity()
//            } while (parser.match(TokenType.COMMA));
//
//            parser.eat(TokenType.RIGHT_BRACKET);
//
//            Slice subslice = new Slice();
//            for (Int i : indices) {
//                subslice.add(left.get(i.value()));
//            }
//
//            return subslice;
//        }
//
//        parser.eat(TokenType.RIGHT_BRACKET);
//
//        return left.get(index.value());
        return UndefExpr.VALUE;
    }
}
