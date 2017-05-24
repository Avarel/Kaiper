package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.VectorAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class VectorParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Expr> exprs = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_BRACKET)) {
            do {
                exprs.add(parser.parseExpr());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return new VectorAtom(exprs);
    }
}
