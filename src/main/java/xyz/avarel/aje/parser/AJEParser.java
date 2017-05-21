package xyz.avarel.aje.parser;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.UndefAtom;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public class AJEParser extends Parser {
    private final ObjectPool pool;

    public AJEParser(AJELexer tokens) {
        this(tokens, new ObjectPool());
    }

    public AJEParser(AJELexer tokens, ObjectPool objectPool) {
        super(tokens, DefaultGrammar.INSTANCE);
        this.pool = objectPool;
    }

    public Expr compile() {
        Expr expr = statements(pool);

        if (!getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw error("Did not parse " + t.getText() + t.getPosition());
            }
        }

        return expr;
    }

    public Expr statements(ObjectPool pool) {
        if (match(TokenType.EOF)) return UndefAtom.VALUE;

        Expr any = parseExpr(pool);

        while (match(TokenType.LINE) || match(TokenType.SEMICOLON)) {
            if (match(TokenType.EOF)) break;

            any = any.andThen(parseExpr(pool));
        }

        return any;
    }

    public Expr block(ObjectPool pool) {
        if (match(TokenType.EOF)) return UndefAtom.VALUE;

        Expr any = parseExpr(pool);

        while (match(TokenType.LINE) || match(TokenType.SEMICOLON)) {
            if (nextIs(TokenType.RIGHT_BRACE)) break;
            if (match(TokenType.EOF)) break;

            any = any.andThen(parseExpr(pool));
        }

        return any;
    }

    public Expr parseExpr(ObjectPool objectPool) {
        return parseExpr(0, objectPool);
    }

    public Expr parseExpr(int precedence, ObjectPool pool) {
        Token token = eat();

        Expr expr = parsePrefix(token, pool);

        return parseInfix(precedence, expr, pool);
    }

    public Expr parsePrefix(Token token, ObjectPool pool) {
        PrefixParser prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw error("Could not parse token `" + token.getText() + "`" + token.getPosition());

        return prefix.parse(this, pool, token);
    }

    public Expr parseInfix(int precedence, Expr left, ObjectPool pool) {
        while (precedence < getPrecedence()) { // ex plus is 6, next is mult which is 7, parse it
            Token token = eat();

            InfixParser infix = getInfixParsers().get(token.getType());

            if (infix == null) throw error("Could not parse token `" + token.getText() + "`" + token.getPosition());

            left = infix.parse(this, pool, left, token);
        }
        return left;
    }
}
