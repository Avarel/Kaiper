package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.ValueExpr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.types.compiled.CompiledFunction;

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
                while (!parser.match(TokenType.RIGHT_BRACE)) {
                    tokens.add(parser.eat());
                }
            } else { // fun(x) = x + 1
                while (parser.peek(0).getType() != TokenType.LINE
                        && parser.peek(0).getType() != TokenType.EOF) {
                    tokens.add(parser.eat());
                }
            }
        } else { // fun(x) { x + 1 }
            parser.eat(TokenType.LEFT_BRACE);
            while (!parser.match(TokenType.RIGHT_BRACE)) {
                tokens.add(parser.eat());
            }
        }

        CompiledFunction function = new CompiledFunction(params, tokens, parser.getObjects());

        if (name != null) {
            parser.getObjects().put(name, function);
        }

        return new ValueExpr(function);
    }
}
