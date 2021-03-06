package xyz.avarel.kaiper.parser.parslets;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ModuleNode;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.PrefixParser;

public class ModuleParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        Expr expr = NullNode.VALUE;

        if (parser.match(TokenType.LEFT_BRACE)) {
            if (parser.match(TokenType.RIGHT_BRACE)) {
                expr = NullNode.VALUE;
            } else {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            }
        }

        return new ModuleNode(token.getPosition(), name, expr);
    }
}
