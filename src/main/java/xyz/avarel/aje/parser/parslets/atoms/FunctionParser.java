package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.ValueAtom;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.functions.CompiledFunction;

import java.util.ArrayList;
import java.util.List;

public class FunctionParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
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

        if (parser.match(TokenType.ASSIGN)) {
            if (parser.match(TokenType.LEFT_BRACE)) { // fun(x) = { x + 1 }
                parseBlock(parser, tokens);
            } else { // fun(x) = x + 1
                while (parser.peek(0).getType() != TokenType.LINE
                        && parser.peek(0).getType() != TokenType.EOF) {
                    tokens.add(parser.eat());
                }
            }
        } else { // fun(x) { x + 1 }
            parser.eat(TokenType.LEFT_BRACE);
            parseBlock(parser, tokens);
        }

        CompiledFunction function = new CompiledFunction(params, tokens, parser.getObjects());

        if (name != null) {
            parser.getObjects().put(name, function);
        }

        return new ValueAtom(function);
    }

    private void parseBlock(AJEParser parser, List<Token> target) {
        int level = 0;
        while (true) {
            Token t = parser.eat();

            if (t.getType() == TokenType.LEFT_BRACE) {
                level++;
            } else if (t.getType() == TokenType.RIGHT_BRACE) {
                if (level > 0) {
                    level--;
                } else {
                    break;
                }
            }

            target.add(t);
        }
    }
}
