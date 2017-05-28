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

public class LambdaFunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Parameter> parameters = new ArrayList<>();

        if (!parser.match(TokenType.ARROW)) {
            Set<String> paramNames = new HashSet<>();

            do {
                String parameterName = parser.eat(TokenType.NAME).getText();

                if (paramNames.contains(parameterName)) {
                    throw new SyntaxException("Duplicate parameter names", parser.getLast().getPosition());
                } else {
                    paramNames.add(parameterName);
                }

                Expr parameterType = new ValueAtom(parser.peek(0).getPosition(), Obj.TYPE);

                if (parser.match(TokenType.COLON)) {
                    Token typeToken = parser.eat(TokenType.NAME);
                    parameterType = new NameAtom(typeToken.getPosition(), typeToken.getText());
                    while (parser.match(TokenType.DOT)) {
                        parameterType = new NameAtom(typeToken.getPosition(), parameterType, parser.eat(TokenType.NAME).getText());
                    }
                }

                Parameter parameter = new Parameter(parameterName, parameterType);
                parameters.add(parameter);
            } while (parser.match(TokenType.COMMA));

            parser.eat(TokenType.ARROW);
        }

        Expr expr = parser.parseStatements();

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionAtom(token.getPosition(), parameters, expr);
    }
}
