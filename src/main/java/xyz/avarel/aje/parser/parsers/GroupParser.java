package xyz.avarel.aje.parser.parsers;

import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.types.Any;

public class GroupParser implements PrefixParser {
    @Override
    public Any parse(AJEParser parser, Token token) {

//        int ahead = 0;
//        while (parser.peek(ahead).getType() != TokenType.EOF
//                && parser.peek(ahead).getType() != TokenType.RIGHT_PAREN) {
//            ahead++;
//        }
//
//        if (parser.peek(ahead + 1).getType() == TokenType.ARROW) {
//            List<String> params = new ArrayList<>();
//            List<Token> tokens = new ArrayList<>();
//
//            if (!parser.match(TokenType.RIGHT_PAREN)) {
//                do {
//                    Token t = parser.eat(TokenType.NAME);
//                    params.add(t.getText());
//                } while (parser.match(TokenType.COMMA));
//                parser.match(TokenType.RIGHT_PAREN);
//            }
//
//            parser.eat(TokenType.ARROW);
//
//            parser.eat(TokenType.LEFT_BRACE);
//            while (!parser.match(TokenType.RIGHT_BRACE)) {
//                tokens.add(parser.eat());
//            }
//
//            return new CompiledFunction(params, tokens);
//        }

        Any expression = parser.parse();
        parser.eat(TokenType.RIGHT_PAREN);
        return expression;
    }
}
