package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
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

        ObjectPool subPool = parser.getObjectPool().subPool();

        Expr expr;

        if (!parser.match(TokenType.LINE)) {
            parser.match(TokenType.ASSIGN);
        }
        if (parser.match(TokenType.LEFT_BRACE)) {
            expr = parser.block(subPool);
            parser.eat(TokenType.RIGHT_BRACE);
        } else {
            expr = parser.statements(subPool);
        }

        CompiledFunction function = new CompiledFunction(params, expr, subPool);

        if (name != null) {
            pool.put(name, function);
        }

        return new FunctionAtom(function);
    }
}
