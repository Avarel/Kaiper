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

public class LambdaParser implements PrefixParser<Expr> {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<String> parameters = new ArrayList<>();
        List<Token> scriptTokens = new ArrayList<>();

        boolean implicit = false;

        if (parser.match(TokenType.LEFT_PAREN)) {
            if (!parser.match(TokenType.RIGHT_PAREN)) {
                do {
                    Token t = parser.eat(TokenType.NAME);
                    parameters.add(t.getText());
                } while (parser.match(TokenType.COMMA));
                parser.eat(TokenType.RIGHT_PAREN);
            }
            parser.eat(TokenType.ARROW);
        }

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

            if (t.getText().equals("it")) implicit = true;

            scriptTokens.add(t);
        }

        if (!parameters.contains("it") && implicit) parameters.add("it");

        return new ValueAtom(new CompiledFunction(parameters, scriptTokens, parser.getObjects()));
    }
}
