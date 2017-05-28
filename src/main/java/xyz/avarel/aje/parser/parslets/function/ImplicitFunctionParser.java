package xyz.avarel.aje.parser.parslets.function;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.InfixParser;
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
        ParserProxy ip = new ParserProxy(parser, token);

        Expr expr = ip.parseInfix(0, new NameAtom(token.getPosition(), token.getText()));

        List<Parameter> list = new ArrayList<>();

        for (String param : ip.parameters) {
            list.add(new Parameter(param));
        }

        return new FunctionAtom(token.getPosition(), list, expr);
    }

    private class ParserProxy extends AJEParser {
        private AJEParser proxy;
        private AJEParser current;
        private final Set<String> parameters = new LinkedHashSet<>();

        public ParserProxy(AJEParser proxy, Token token) {
            super(proxy);

            this.proxy = proxy;
            this.current = this;
            this.parameters.add(token.getText());
        }

        @Override
        public Token eat() {
            Token token = super.eat();
            if (token.getType() == TokenType.UNDERSCORE) {
                parameters.add(token.getText());
                return new Token(token.getPosition(), TokenType.NAME, token.getText());
            }
            return token;
        }

        @Override
        public Expr parseInfix(int precedence, Expr left) {
            while (precedence < getPrecedence()) {
                Token token = eat();

                if (token.getType() == TokenType.PIPE_FORWARD) {
                    current = proxy;
                }

                InfixParser infix = getInfixParsers().get(token.getType());

                if (infix == null) throw new SyntaxException("Could not parse token `" + token.getText() + "`", token.getPosition());

                left = infix.parse(current, left, token);
            }
            return left;
        }
    }
}
