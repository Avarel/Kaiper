package xyz.avarel.kaiper.parser.parslets;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.TypeNode;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.PatternParser;
import xyz.avarel.kaiper.parser.PrefixParser;

/**
 * type IDENTIFIER(params...) : SUPERTYPE(super params...) {
 *     // constructor expr
 * }
 */

public class TypeParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        PatternCase patternCase = PatternCase.EMPTY;
        if (parser.match(TokenType.LEFT_PAREN)) {
            patternCase = new PatternParser(parser).parsePatternCase();
            parser.eat(TokenType.RIGHT_PAREN);
        }

        Expr constructorExpr = NullNode.VALUE;
        if (parser.nextIs(TokenType.LEFT_BRACE)) {
            constructorExpr = parser.parseBlock();
        }

        return new TypeNode(token.getPosition(), name, patternCase, constructorExpr);
    }
}
