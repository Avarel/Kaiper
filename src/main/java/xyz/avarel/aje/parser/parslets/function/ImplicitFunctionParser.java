package xyz.avarel.aje.parser.parslets.function;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ImplicitFunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        ParserProxy ip = new ParserProxy(parser);
        ip.params.add(token.getText());

        Expr expr = ip.parseInfix(0, new NameAtom(token.getText()));

        List<Parameter> list = new ArrayList<>();

        for (String param : ip.params) {
            list.add(new Parameter(param));
        }

        return new FunctionAtom(list, expr);
    }

    private class ParserProxy extends AJEParser {
        private final Set<String> params = new LinkedHashSet<>();

        public ParserProxy(AJEParser parser) {
            super(parser);
        }

        @Override
        public Token eat() {
            Token token = super.eat();
            if (token.getType() == TokenType.UNDERSCORE) {
                params.add(token.getText());
                return new Token(token.getPosition(), TokenType.NAME, token.getText());
            }
            return token;
        }
    }
}
