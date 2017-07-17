package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ModuleNode;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;

public class ModuleParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        String name = parser.eat(TokenType.IDENTIFIER).getString();

        Expr expr = UndefinedNode.VALUE;

        if (parser.match(TokenType.LEFT_BRACE)) {
            if (parser.match(TokenType.RIGHT_BRACE)) {
                expr = UndefinedNode.VALUE;
            } else {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            }
        }

        return new ModuleNode(name, expr);
    }
}
