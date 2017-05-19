package xyz.avarel.aje.parser.lexer;

public class Position {
    private final long line;
    private final long lineIndex;
    private final long index;

    public Position(long index, long line, long lineIndex) {
        this.index = index;
        this.line = line;
        this.lineIndex = lineIndex;
    }

    @Override
    public String toString() {
        return " at " + this.index + " [line " + this.line + " : char " + this.lineIndex + "]";
    }
}
