package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.ast.TypeNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.AJEParserUtils;
import xyz.avarel.aje.parser.PrefixParser;

import java.util.Collections;
import java.util.List;

/**
 * type IDENTIFIER(params...) : SUPERTYPE(super params...) {
 *     // constructor expr
 * }
 */

public class TypeParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        List<ParameterData> parameters = Collections.emptyList();
        if (parser.nextIs(TokenType.LEFT_PAREN)) {
            parameters = AJEParserUtils.parseParameters(parser);
        }

        Identifier superType = new Identifier(new Identifier("Object"), "TYPE");
        List<Single> superParameters = Collections.emptyList();
        if (parser.match(TokenType.COLON)) {
            superType = parser.parseIdentifier();

            if (parser.nextIs(TokenType.LEFT_PAREN)) {
                superParameters = AJEParserUtils.parseArguments(parser);
            }
        }

        Expr constructorExpr = UndefinedNode.VALUE;
        if (parser.nextIs(TokenType.LEFT_BRACE)) {
            constructorExpr = AJEParserUtils.parseBlock(parser);
        }

        return new TypeNode(name, parameters, superType, superParameters, constructorExpr);
    }
}
