package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
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

        if (!parser.match(TokenType.ARROW)) {
            do {
                Token t = parser.eat(TokenType.NAME);
                parameters.add(t.getText());
            } while (parser.match(TokenType.COMMA));

            parser.eat(TokenType.ARROW);
        }

        ObjectPool subPool = pool.subPool();

        Expr expr = parser.block(subPool);

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionAtom(new CompiledFunction(parameters, expr, subPool));
    }
}
