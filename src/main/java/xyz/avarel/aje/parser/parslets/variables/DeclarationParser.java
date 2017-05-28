package xyz.avarel.aje.parser.parslets.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.UndefAtom;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class DeclarationParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        Token name = parser.eat(TokenType.NAME);

        if (parser.match(TokenType.ASSIGN)) {
            return new AssignmentExpr(true, name.getText(), parser.parseExpr());
        }

        return new AssignmentExpr(true, name.getText(), UndefAtom.VALUE);
    }
}
