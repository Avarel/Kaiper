package xyz.avarel.aje.parserRewrite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Lexer implements Iterator<Token> {
    private final Map<Character, TokenType> punctuators = new HashMap<>();
    private final String str;
    private int i = 0;

    /**
     * Creates a new Lexer to tokenize the given string.
     *
     * @param str String to tokenize.
     */
    public Lexer(String str) {
        i = 0;
        this.str = str;

        for (TokenType type : TokenType.values()) {
            Character punctuator = type.punctuator();
            if (punctuator != null) {
                punctuators.put(punctuator, type);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Token next() {
        while (i < str.length()) {
            char c = str.charAt(i++);

            if (Character.isDigit(c)) {
                int start = i - 1;

                boolean point = false;

                while (i < str.length()) {
                    if (str.charAt(i) == '.') {
                        if (!(i >= str.length() - 1) && !Character.isDigit(str.charAt(i + 1))) {
                            break;
                        } else if (!point) {
                            point = true;
                        } else {
                            break;
                        }
                    } else if (!Character.isDigit(str.charAt(i))) {
                        break;
                    }
                    i++;
                }

                String value = str.substring(start, i);
                return new Token(TokenType.NUMERIC, value);

            } else if (punctuators.containsKey(c)) {
                return new Token(punctuators.get(c), Character.toString(c));
            } else if (Character.isLetter(c)) {
                int start = i - 1;
                while (i < str.length()) {
                    if (!Character.isLetter(str.charAt(i))) {
                        break;
                    }
                    i++;
                }
                String name = str.substring(start, i);
                return new Token(TokenType.NAME, name);
            } else {
                if (!Character.isSpaceChar(c)) return new Token(TokenType.UNKNOWN, Character.toString(c) + "_?");
            }
        }
        return new Token(TokenType.EOF);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        while (hasNext()) {
            Token t = next();
            if (t.getType() == TokenType.EOF) break;
            s.append(t).append("    ");
        }
        return s.toString();
    }
}