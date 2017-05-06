package xyz.hexav.aje.expressions;

/**
 * Provides useful methods to tokenize and to iterate
 * through a String.
 */
public class AJELexer {
    private int pos = -1;
    private char current;
    private final String target;

    public AJELexer(String target) {
        this.target = target;
    }

    /**
     * Get the next character and increase the position variable.
     */
    public void advance() {
        current = ++pos < target.length() ? target.charAt(pos) : (char) -1;
    }

    /**
     * Consume and return true if the next meaningful character is the prompt.
     */
    public boolean consume(char prompt) {
        return consume(prompt, false);
    }

    /**
     * @param prompt Character prompt.
     * @param strict If should skip whitespaces.
     * @return If the next character (skip whitespaces if not strict) is the prompt.
     */
    public boolean consume(char prompt, boolean strict) {
        if (!strict) skipWhitespace();
        if (current == prompt) {
            advance();
            return true;
        }
        return false;
    }

    /**
     * Return true if the next characters in prompt.length() range is equal to prompt.
     */
    public boolean consume(String prompt) {
        // FASTPATH: Use less expensive method of consuming a character.
        if (prompt.length() == 1) return consume(prompt.charAt(0));
        skipWhitespace();
        String text = target.substring(pos, Math.min(pos + prompt.length(), target.length()));

        if (text.equals(prompt)) {
            pos = pos + prompt.length() - 1;
            advance();
            skipWhitespace();
            return true;
        }

        return false;
    }

    public boolean nextIs(String prompt) {
        // Use less expensive method of consuming a character.
        if (prompt.length() == 1) return nextIs(prompt.charAt(0));

        skipWhitespace();

        String text = target.substring(pos, Math.min(pos + prompt.length(), target.length()));
        return text.equals(prompt);
    }

    /**
     * Resets the position of the compiling unit.
     */
    public void reset() {
        pos = -1;
        current = (char) -1;
    }

    /**
     * Skip whitespaces.
     */
    public void skipWhitespace() {
        while (current == ' ') advance();
    }

    /**
     * consume(prompt) without consuming the character.
     */
    public boolean nextIs(char prompt) {
        skipWhitespace();
        return current == prompt;
    }

    public int pos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String string() {
        return target;
    }

    public char currentChar() {
        return current;
    }

    public String currentInfo() {
        return "['" + target + "':" + pos + "]";
    }

    // next string
    public String nextString() {
        int start = pos;
        while (Character.isAlphabetic(current)) advance();
        return target.substring(start, pos);
    }

    public String nextNumber() {
        int start = pos;
        while (Character.isDigit(current)) advance();
        return target.substring(start, pos);
    }
}
