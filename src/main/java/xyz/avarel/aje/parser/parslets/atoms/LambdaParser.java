package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.List;

public class LambdaParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Parameter> parameters = new ArrayList<>();

        if (!parser.match(TokenType.ARROW)) {
            do {
                String p_name = parser.eat(TokenType.NAME).getText();
                Expr p_type = new ValueAtom(Obj.TYPE);

                if (parser.match(TokenType.COLON)) {
                    p_type = parser.parseExpr(Precedence.ATTRIBUTE);
                }

                Parameter parameter = new Parameter(p_name, p_type);
                parameters.add(parameter);
            } while (parser.match(TokenType.COMMA));

            parser.eat(TokenType.ARROW);
        }

        Expr expr = parser.parseBlock();

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionAtom(parameters, expr);
    }
}
