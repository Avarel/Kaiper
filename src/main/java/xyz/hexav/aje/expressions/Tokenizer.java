package xyz.hexav.aje.expressions;

/**
 * Provides useful methods to tokenize and to iterate
 * through a String.
 */
public class Tokenizer {
    private int pos = -1;
    private char current;
    private final String target;

    public Tokenizer(String target) {
        this.target = target;
    }

    /**
     * Get the next character and increase the position variable.
     */
    public char nextChar() {
        return current = ++pos < target.length() ? target.charAt(pos) : (char) -1;
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
            nextChar();
            return true;
        }
        return false;
    }

    /**
     * Return true if the next characters in prompt.length() range is equal to prompt.
     */
    protected boolean consume(String prompt) {
        // FASTPATH: Use less expensive method of consuming a character.
        if (prompt.length() == 1) return consume(prompt.charAt(0));
        skipWhitespace();
        String text = target.substring(pos, Math.min(pos + prompt.length(), target.length()));

        if (text.equals(prompt)) {
            pos = pos + prompt.length() - 1;
            current = nextChar();
            skipWhitespace();
            return true;
        }

        return false;
    }

    protected boolean nextIs(String prompt) {
        // Use less expensive method of consuming a character.
        if (prompt.length() == 1) return nextIs(prompt.charAt(0));

        skipWhitespace();

        String text = target.substring(pos, Math.min(pos + prompt.length(), target.length()));
        return text.equals(prompt);
    }

    /**
     * Resets the position of the compiling unit.
     */
    protected void resetPosition() {
        pos = -1;
    }

    /**
     * Skip whitespaces.
     */
    public void skipWhitespace() {
        while (current == ' ' || current == '\n') nextChar();
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

    public String target() {
        return target;
    }

    public char currentChar() {
        return current;
    }

    public void setCurrentChar(char current) {
        this.current = current;
    }

    protected String currentInfo() {
        return "['" + target + "':" + pos + "]";
    }
}
