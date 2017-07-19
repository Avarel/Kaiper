package xyz.avarel.kaiper.tools.bytecode;

public class OutlineOptions {
    private boolean skipVersionHeader = false;
    private boolean skipVersionChecking = false;
    private boolean skipHeaderChecking = false;
    private boolean skipStringPool = false;
    private boolean dontPrintPoolValues = false;
    private boolean dontExitOnHeaderError = false;
    private boolean inlineStrings = true;
    private boolean use4Spaces = false;
    private boolean explicitEnd = false;

    public OutlineOptions() {
    }

    public OutlineOptions(OutlineOptions options) {
        this.skipVersionHeader = options.skipVersionHeader;
        this.skipVersionChecking = options.skipVersionChecking;
        this.skipHeaderChecking = options.skipHeaderChecking;
        this.skipStringPool = options.skipStringPool;
        this.dontPrintPoolValues = options.dontPrintPoolValues;
        this.dontExitOnHeaderError = options.dontExitOnHeaderError;
        this.inlineStrings = options.inlineStrings;
        this.use4Spaces = options.use4Spaces;
        this.explicitEnd = options.explicitEnd;
    }

    public boolean skipVersionHeader() {
        return skipVersionHeader;
    }

    public OutlineOptions skipVersionHeader(boolean skipVersionHeader) {
        this.skipVersionHeader = skipVersionHeader;
        return this;
    }

    public boolean skipVersionChecking() {
        return skipVersionChecking;
    }

    public OutlineOptions skipVersionChecking(boolean skipVersionChecking) {
        this.skipVersionChecking = skipVersionChecking;
        return this;
    }

    public boolean skipHeaderChecking() {
        return skipHeaderChecking;
    }

    public OutlineOptions skipHeaderChecking(boolean skipHeaderChecking) {
        this.skipHeaderChecking = skipHeaderChecking;
        return this;
    }

    public boolean skipStringPool() {
        return skipStringPool;
    }

    public OutlineOptions skipStringPool(boolean skipStringPool) {
        this.skipStringPool = skipStringPool;
        return this;
    }

    public boolean dontPrintPoolValues() {
        return dontPrintPoolValues;
    }

    public OutlineOptions dontPrintPoolValues(boolean dontPrintPoolValues) {
        this.dontPrintPoolValues = dontPrintPoolValues;
        return this;
    }

    public boolean dontExitOnHeaderError() {
        return dontExitOnHeaderError;
    }

    public OutlineOptions dontExitOnHeaderError(boolean dontExitOnHeaderError) {
        this.dontExitOnHeaderError = dontExitOnHeaderError;
        return this;
    }

    public boolean inlineStrings() {
        return inlineStrings;
    }

    public OutlineOptions inlineStrings(boolean inlineStrings) {
        this.inlineStrings = inlineStrings;
        return this;
    }

    public boolean use4Spaces() {
        return use4Spaces;
    }

    public OutlineOptions use4Spaces(boolean use4Spaces) {
        this.use4Spaces = use4Spaces;
        return this;
    }

    public boolean explicitEnd() {
        return explicitEnd;
    }

    public OutlineOptions explicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
        return this;
    }
}
