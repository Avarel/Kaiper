package xyz.hexav.aje.expressions;

/**
 * Provides useful methods to tokenize and to iterate
 * through a String.
 */
abstract class TokenizingUnit {
    protected int pos = -1;
    protected char current;
    protected String line;

    /**
     * Get the next character and increase the position variable.
     */
    protected char nextChar() {
        return current = ++pos < line.length() ? line.charAt(pos) : (char) -1;
    }

    /**
     * Consume and return true if the next meaningful character is the prompt.
     */
    protected boolean consume(char prompt) {
        return consume(prompt, false);
    }

    /**
     * @param prompt Character prompt.
     * @param strict If should skip whitespaces.
     * @return If the next character (skip whitespaces if not strict) is the prompt.
     */
    protected boolean consume(char prompt, boolean strict) {
        if (!strict) skipWS();
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
        // Use less expensive method of consuming a character.
        if (prompt.length() == 1) return consume(prompt.charAt(0));

        skipWS();

        int start = pos;
        int _pos = start + prompt.length();
        if (_pos > line.length()) return false;

        String text = line.substring(start, _pos);

        if (text.equals(prompt)) {
            pos = _pos - 1;
            current = nextChar();
            skipWS();
            return true;
        }

        return false;
    }

    protected boolean nextIs(String prompt) {
        // Use less expensive method of consuming a character.
        if (prompt.length() == 1) return nextIs(prompt.charAt(0));

        skipWS();

        int _pos = pos + prompt.length();
        if (_pos > line.length()) return false;

        String text = line.substring(pos, _pos);

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
    protected void skipWS() {
        while (current == ' ' || current == '\n') nextChar();
    }

    /**
     * consume(prompt) without consuming the character.
     */
    protected boolean nextIs(char prompt) {
        skipWS();
        return current == prompt;
    }

    public int getPos() {
        return pos;
    }

    public String getLine() {
        return line;
    }

    /**
     * Set target compiling line.
     */
    protected void setLine(String line) {
        this.line = line;
    }

    public char getCurrentChar() {
        return current;
    }

    protected String dumpInfo() {
        return "['" + line + "':" + pos + "]";
    }
}
