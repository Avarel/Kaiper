package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class Grammar {
    private final Map<TokenType, PrefixParser> prefixParsers;
    private final Map<TokenType, InfixParser> infixParsers;

    public Grammar() {
        this(new HashMap<>(), new HashMap<>());
    }

    public Grammar(Map<TokenType, PrefixParser> prefixParsers, Map<TokenType, InfixParser> infixParsers) {
        this.prefixParsers = new HashMap<>(prefixParsers);
        this.infixParsers = new HashMap<>(infixParsers);
    }

    public void register(TokenType token, PrefixParser parselet) {
        prefixParsers.put(token, parselet);
    }

    public void register(TokenType token, InfixParser parselet) {
        infixParsers.put(token, parselet);
    }

    public Map<TokenType, PrefixParser> getPrefixParsers() {
        return prefixParsers;
    }

    public Map<TokenType, InfixParser> getInfixParsers() {
        return infixParsers;
    }

    public Grammar copy() {
        return new Grammar(prefixParsers, infixParsers);
    }
}
