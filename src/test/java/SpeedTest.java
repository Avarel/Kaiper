import junit.framework.TestCase;
import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.MathExpression;

public class SpeedTest extends TestCase {
    public void testSpeeds() {
        // Performance hoggers
        // [1,2,[3,4,[5,6,[7,8],90, [91, 92],100]],9,10,[11, [50,52],12],13]
        // [0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]@[0..10]
        String script = "1+2*3/4";
        int testsAmt = 10000;

        System.out.println(testsAmt + " Tests");
        System.out.println("Precompiled: " + testPrecompiledSpeed(script, testsAmt) + "ns avg");
        System.out.println("Recompile: " + testRecompileSpeed(script, testsAmt) + "ns avg");
    }

    private long testPrecompiledSpeed(String script, long tests) {
        MathExpression exp = new ExpressionBuilder().addLine(script).build().compile();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            exp.eval();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }

    private long testRecompileSpeed(String script, long tests) {
        MathExpression exp = new ExpressionBuilder().addLine(script).build();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            exp.forceCompile().eval();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }
}