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

public class FunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, ObjectPool pool, Token token) {
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

        ObjectPool f_pool =  parser.getObjectPool().subpool();

        Expr expr;

        parser.match(TokenType.ASSIGN);
        if(parser.match(TokenType.LEFT_BRACE)) {
            expr = parser.compile(f_pool, false);
            parser.eat(TokenType.RIGHT_BRACE);
        } else {
            expr = parser.compile(f_pool, false);
        }

        CompiledFunction function = new CompiledFunction(params, expr, f_pool);

        if (name != null) {
            pool.put(name, function);
        }

        return new ValueAtom(function);
    }
}
