package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.NameAtom;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.ast.variables.AttributeExpr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.List;

public class FunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Parameter> parameters = new ArrayList<>();

        String name = null;
        if (parser.match(TokenType.NAME)) {
            name = parser.getLast().getText();
        }

        parser.eat(TokenType.LEFT_PAREN);
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                String p_name = parser.eat(TokenType.NAME).getText();
                Expr p_type = new ValueAtom(Obj.TYPE);
                Expr p_def = null;

                if (parser.match(TokenType.COLON)) {
                    p_type = new NameAtom(parser.eat(TokenType.NAME).getText());
                    while (parser.match(TokenType.DOT)) {
                        p_type = new AttributeExpr(p_type, parser.eat(TokenType.NAME).getText());
                    }
                }
                if (parser.match(TokenType.ASSIGN)) {
                    p_def = parser.parseExpr();
                }

                Parameter parameter = new Parameter(p_name, p_type, p_def);
                parameters.add(parameter);
            } while (parser.match(TokenType.COMMA));
            parser.match(TokenType.RIGHT_PAREN);
        }

        Expr expr;

        if (parser.match(TokenType.ASSIGN)) {
            if (parser.match(TokenType.LEFT_BRACE)) {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            } else {
                expr = parser.parseExpr();
            }
        } else if (parser.match(TokenType.LEFT_BRACE)) {
            expr = parser.parseStatements();
            parser.eat(TokenType.RIGHT_BRACE);
        } else {
            expr = parser.parseStatements();
        }

        return new FunctionAtom(name, parameters, expr);
    }
}
