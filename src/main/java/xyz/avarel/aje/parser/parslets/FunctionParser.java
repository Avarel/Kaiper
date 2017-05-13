package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.compiled.CompiledFunction;

import java.util.ArrayList;
import java.util.List;

public class FunctionParser implements PrefixParser<AJEFunction> {
    @Override
    public AJEFunction parse(AJEParser parser, Token token) {
        List<String> params = new ArrayList<>();
        List<Token> tokens = new ArrayList<>();

        String name = null;
        if (parser.match(TokenType.NAME)) {
            name = parser.getLast().getText();
        }

        parser.eat(TokenType.LEFT_PAREN);
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                Token t = parser.eat(TokenType.NAME);
                params.add(t.getText());
            } while (parser.match(TokenType.COMMA));
            parser.match(TokenType.RIGHT_PAREN);
        }

        parser.eat(TokenType.LEFT_BRACE);
        while (!parser.match(TokenType.RIGHT_BRACE)) {
            tokens.add(parser.eat());
        }

        CompiledFunction function = new CompiledFunction(params, tokens);

        if (name != null) {
            parser.getObjects().put(name, function);
        }

        return function;
    }
}
