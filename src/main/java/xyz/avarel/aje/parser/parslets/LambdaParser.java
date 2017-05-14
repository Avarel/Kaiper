package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.Parser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.compiled.CompiledFunction;

import java.util.ArrayList;
import java.util.List;

public class LambdaParser implements PrefixParser<CompiledFunction> {
    @Override
    public CompiledFunction parse(AJEParser parser, Token token) {
        List<Token> parameterTokens = new ArrayList<>();
        List<Token> scriptTokens = new ArrayList<>();

        boolean implicit = false;
        boolean arrow = false;

        while (!parser.match(TokenType.RIGHT_BRACE)) {
            if (parser.match(TokenType.ARROW)) {
                if (arrow) {
                    throw parser.error("Unexpected token");
                }
                parameterTokens.addAll(scriptTokens);
                scriptTokens.clear();
                arrow = true;
            } else {
                Token t = parser.eat();
                if (t.getType() == TokenType.NAME && t.getText().equals("it")) {
                    implicit = true;
                }
                scriptTokens.add(t);
            }
        }

        List<String> parameters = new ArrayList<>();
        if (parameterTokens.isEmpty()) {
            if (implicit) {
                parameters.add("it");
            }
        } else {
            Parser parameterParser = new Parser(parameterTokens);

            if (parameterParser.getLexer().hasNext()) {
                do {
                    parameters.add(parameterParser.eat(TokenType.NAME).getText());
                } while (parameterParser.match(TokenType.COMMA));

                if (!parameterParser.getTokens().isEmpty()) {
                    throw parameterParser.error("Unexpected token", parameterParser.getTokens().get(0).getPos());
                }
            }
        }

        return new CompiledFunction(parameters, scriptTokens, parser.getObjects());
    }
}
