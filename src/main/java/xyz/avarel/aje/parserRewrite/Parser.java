package xyz.avarel.aje.parserRewrite;

import java.util.*;

public abstract class Parser {
    private final Iterator<Token> mTokens;
    private final List<Token> mRead = new ArrayList<Token>();
    private final Map<TokenType, PrefixParselet> prefixParsers = new HashMap<>();
    private final Map<TokenType, InfixParser> infixParsers = new HashMap<>();

    public Parser(Iterator<Token> tokens) {
        mTokens = tokens;
    }

    public void register(TokenType token, PrefixParselet parselet) {
        prefixParsers.put(token, parselet);
    }

    public void register(TokenType token, InfixParser parselet) {
        infixParsers.put(token, parselet);
    }

    public Map<TokenType, PrefixParselet> getPrefixParsers() {
        return prefixParsers;
    }

    public Map<TokenType, InfixParser> getInfixParsers() {
        return infixParsers;
    }

    public boolean match(TokenType expected) {
        Token token = peek(0);
        if (token.getType() != expected) {
            return false;
        }
        eat();
        return true;
    }

    public Token eat(TokenType expected) {
        Token token = peek(0);
        if (token.getType() != expected) {
            throw new RuntimeException("Expected token " + expected +
                    " and found " + token.getType());
        }
        return eat();
    }

    public Token eat() {
        // Make sure we've read the token.
        peek(0);

        return mRead.remove(0);
    }

    public Token peek(int distance) {
        // Read in as many as needed.
        while (distance >= mRead.size()) {
            mRead.add(mTokens.next());
        }

        // Get the queued token.
        return mRead.get(distance);
    }

    public int getPrecedence() {
        InfixParser parser = infixParsers.get(peek(0).getType());
        if (parser != null) return parser.getPrecedence();

        return 0;
    }
}
