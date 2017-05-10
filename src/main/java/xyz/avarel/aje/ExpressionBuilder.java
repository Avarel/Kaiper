package xyz.avarel.aje;

public class ExpressionBuilder extends AbstractBuilder<ExpressionBuilder> {
    public ExpressionBuilder() {
        this(null);
    }

    public ExpressionBuilder(String script) {
        if (script != null) addLine(script);
    }

    public MathExpression build() {
        return new MathExpression(getScript(), getObjects());
    }
}
