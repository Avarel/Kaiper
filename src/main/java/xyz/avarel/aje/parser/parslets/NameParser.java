package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.NameAtom;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class NameParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (parser.match(TokenType.ASSIGN)) {
            return new AssignmentExpr(token.getText(), parser.parseExpr(Precedence.ASSIGNMENT));
        }

        return new NameAtom(token.getText());
    }
}
