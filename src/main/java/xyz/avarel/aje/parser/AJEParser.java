package xyz.avarel.aje.parser;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.UndefAtom;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

public class AJEParser extends Parser {
    public AJEParser(AJELexer tokens) {
        super(tokens, DefaultGrammar.INSTANCE);
    }

    public Expr compile() {
        Expr expr = parseStatements();

        if (!getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw error("Did not parse " + t.getText() + t.getPosition());
            }
        }

        return expr;
    }

    public Expr parseStatements() {
        if (match(TokenType.EOF)) return UndefAtom.VALUE;

        Expr any = parseExpr();

        while (match(TokenType.LINE) || match(TokenType.SEMICOLON)) {
            if (match(TokenType.EOF)) break;

            any = any.andThen(parseExpr());
        }

        return any;
    }

    public Expr parseBlock() {
        if (match(TokenType.EOF)) return UndefAtom.VALUE;

        Expr any = parseExpr();

        while (match(TokenType.LINE) || match(TokenType.SEMICOLON)) {
            if (nextIs(TokenType.RIGHT_BRACE)) break;
            if (match(TokenType.EOF)) break;

            any = any.andThen(parseExpr());
        }

        return any;
    }

    public Expr parseExpr() {
        return parseExpr(0);
    }

    public Expr parseExpr(int precedence) {
        Token token = eat();

        Expr expr = parsePrefix(token);

        return parseInfix(precedence, expr);
    }

    public Expr parsePrefix(Token token) {
        PrefixParser prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw error("Could not parse token `" + token.getText() + "`" + token.getPosition());

        return prefix.parse(this, token);
    }

    public Expr parseInfix(int precedence, Expr left) {
        while (precedence < getPrecedence()) { // ex plus is 6, next is mult which is 7, parse it\
            Token token = eat();

            InfixParser infix = getInfixParsers().get(token.getType());

            if (infix == null) throw error("Could not parse token `" + token.getText() + "`" + token.getPosition());

            left = infix.parse(this, left, token);
        }
        return left;
    }
}
