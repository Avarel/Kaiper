import junit.framework.TestCase;
import xyz.avarel.aje.ExpressionBuilder;
import xyz.avarel.aje.MathExpression;

public class SpeedTest extends TestCase {
    public void testSpeeds() {
        // Performance hoggers
        // [1,2,[3,4,[5,6,[7,8],90, [91, 92],100]],9,10,[11, [50,52],12],13]
        // (8+2i)*(5i+3)
        // [1..10] * 10
        // x = fun(x) { x + 2 }; fun y(x) = { x + 2 }; z = { x -> x + 2 }; x(2) == y(2) /\ y(2) == z(2)
        String script = "[1..10]";
        int testsAmt = 10000;

        System.out.println(testsAmt + " Tests");
        System.out.println("Precompiled: " + testPrecompiledSpeed(script, testsAmt) + "ns avg");
        System.out.println("  Recompile: " + testRecompileSpeed(script, testsAmt) + "ns avg");
    }

    private long testPrecompiledSpeed(String script, long tests) {
        MathExpression exp = new ExpressionBuilder().addLine(script).build().compile();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            exp.compute();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }

    private long testRecompileSpeed(String script, long tests) {
        MathExpression exp = new ExpressionBuilder().addLine(script).build();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            //noinspection deprecation
            exp.forceCompile().compute();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }
}
