package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.ast.ConditionalExpr;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class IfElseParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {

        parser.eat(TokenType.LEFT_PAREN);
        Expr condition = parser.parseExpr();
        parser.eat(TokenType.RIGHT_PAREN);

        Expr ifBranch;

        if (parser.match(TokenType.LEFT_BRACE)) {
            ifBranch = parser.parseStatements();
            parser.eat(TokenType.RIGHT_BRACE);
        } else {
            ifBranch = parser.parseExpr();
        }

        Expr elseBranch = null;

        if (parser.match(TokenType.ELSE)) {
            if (parser.match(TokenType.LEFT_BRACE)) {
                elseBranch = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            } else {
                elseBranch = parser.parseExpr();
            }
        }

        return new ConditionalExpr(token.getPosition(), condition, ifBranch, elseBranch);
    }
}
