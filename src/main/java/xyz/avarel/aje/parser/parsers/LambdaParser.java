package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.compiled.CompiledFunction;

import java.util.ArrayList;
import java.util.List;

public class LambdaParser implements PrefixParser<CompiledFunction> {
    @Override
    public CompiledFunction parse(AJEParser parser, Token token) {
        //parser.eat(TokenType.LEFT_BRACE);
        List<Token> parameterTokens = new ArrayList<>();
        List<Token> scriptTokens = new ArrayList<>();

        boolean implicit = false;
        boolean arrow = false;

        while (!parser.match(TokenType.RIGHT_BRACE)) {
            if (parser.match(TokenType.ARROW)) {
                if (arrow) {
                    throw new AJEException("did not expect another arrow");
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
        } else for (Token t : parameterTokens) {
            switch (t.getType()) {
                case COMMA: break;
                case NAME: parameters.add(t.getText()); break;
                default: throw new AJEException("parameters must be names only");
            }
        }

        return new CompiledFunction(parameters, scriptTokens);
    }
}
