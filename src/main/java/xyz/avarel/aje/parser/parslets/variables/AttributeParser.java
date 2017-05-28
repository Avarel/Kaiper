package xyz.avarel.aje.parser.parslets.variables;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class AttributeParser extends BinaryParser {
    public AttributeParser() {
        super(Precedence.ATTRIBUTE);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Token name = parser.eat(TokenType.NAME);

        if (parser.match(TokenType.ASSIGN)) {
            return new AssignmentExpr(token.getPosition(), left, name.getText(), parser.parseExpr());
        }

        return new NameAtom(token.getPosition(), left, name.getText());
    }
}
