package xyz.avarel.kaiper.parser.parslets;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.TypeNode;
import xyz.avarel.kaiper.ast.functions.ParameterData;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.KaiperParserUtils;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.Collections;
import java.util.List;

/**
 * type IDENTIFIER(params...) : SUPERTYPE(super params...) {
 *     // constructor expr
 * }
 */

public class TypeParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        List<ParameterData> parameters = Collections.emptyList();
        if (parser.nextIs(TokenType.LEFT_PAREN)) {
            parameters = KaiperParserUtils.parseParameters(parser);
        }

        Identifier superType = new Identifier(
                token.getPosition(),
                new Identifier(
                        token.getPosition(),
                        "Object"
                ),
                "TYPE"
        );
        List<Single> superParameters = Collections.emptyList();
        if (parser.match(TokenType.COLON)) {
            superType = parser.parseIdentifier();

            if (parser.nextIs(TokenType.LEFT_PAREN)) {
                superParameters = KaiperParserUtils.parseArguments(parser);
            }
        }

        Expr constructorExpr = NullNode.VALUE;
        if (parser.nextIs(TokenType.LEFT_BRACE)) {
            constructorExpr = KaiperParserUtils.parseBlock(parser);
        }

        return new TypeNode(token.getPosition(), name, parameters, superType, superParameters, constructorExpr);
    }
}
