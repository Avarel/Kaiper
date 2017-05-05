import junit.framework.TestCase;
import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.MathExpression;

public class GeneralTest extends TestCase {

    public void testGet() {
        MathExpression exp = new ExpressionBuilder()
                .addLine("val x = 1 + 2")
                .addLine("x + 3")
                .build()
                .compile();

        System.out.println(exp.value());
    }

    public void testSpeeds() {
        String script = "[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]";
        int testsAmt = 10000;

        System.out.println(testsAmt + " Tests");
        System.out.println("Precompiled: " + testPrecompiledSpeed(script, testsAmt) + "ns avg");
        System.out.println("Recompile: " + testRecompileSpeed(script, testsAmt) + "ns avg");
    }

    private long testPrecompiledSpeed(String script, long tests) {
        MathExpression exp = new ExpressionBuilder().addLine(script).build().compile();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            exp.value();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }

    private long testRecompileSpeed(String script, long tests) {
        MathExpression exp = new ExpressionBuilder().addLine(script).build();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            exp.forceCompile().value();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }
}
