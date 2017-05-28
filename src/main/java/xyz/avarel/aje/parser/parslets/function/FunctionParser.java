package xyz.avarel.aje.parser.parslets.function;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Set<String> paramNames = new HashSet<>();
            boolean requireDef = false;

            do {
                String parameterName = parser.eat(TokenType.NAME).getText();

                if (paramNames.contains(parameterName)) {
                    throw new SyntaxException("Duplicate parameter name", parser.getLast().getPosition());
                } else {
                    paramNames.add(parameterName);
                }

                Expr parameterType = new ValueAtom(parser.peek(0).getPosition(), Obj.TYPE);
                Expr parameterDefault = null;

                if (parser.match(TokenType.COLON)) {
                    Token typeToken = parser.eat(TokenType.NAME);
                    parameterType = new NameAtom(typeToken.getPosition(), typeToken.getText());
                    while (parser.match(TokenType.DOT)) {
                        parameterType = new NameAtom(typeToken.getPosition(), parameterType, parser.eat(TokenType.NAME).getText());
                    }
                }
                if (parser.match(TokenType.ASSIGN)) {
                    parameterDefault = parser.parseExpr();
                    requireDef = true;
                } else if (requireDef) {
                    throw new SyntaxException("All parameters after the first default requires a default", parser.peek(0).getPosition());
                }

                Parameter parameter = new Parameter(parameterName, parameterType, parameterDefault);
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

        return new FunctionAtom(token.getPosition(), name, parameters, expr);
    }
}
