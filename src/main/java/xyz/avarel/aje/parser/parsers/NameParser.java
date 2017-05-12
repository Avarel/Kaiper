package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.compiled.CompiledFunction;
import xyz.avarel.aje.types.others.Undefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {
        if(parser.match(TokenType.ARROW)) {
            List<Token> tokens = new ArrayList<>();

            parser.eat(TokenType.LEFT_BRACE);
            while (!parser.match(TokenType.RIGHT_BRACE)) {
                tokens.add(parser.eat());
            }

            return new CompiledFunction(Collections.singletonList(token.getText()), tokens);
        }

        Any obj = parser.getObjects().get(token.getText());
        return obj == null ? Undefined.VALUE : obj;
    }
}
