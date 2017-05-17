package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.ValueAtom;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.functions.CompiledFunction;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.ArrayList;
import java.util.List;

public class LambdaParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {
        List<String> parameters = new ArrayList<>();

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

        ObjectPool f_pool = parser.getObjectPool().subpool();

        Expr expr = parser.compile(f_pool, false);

        parser.eat(TokenType.RIGHT_BRACE);

        System.out.println(f_pool);

        return new ValueAtom(new CompiledFunction(parameters, expr, f_pool));
    }
}
