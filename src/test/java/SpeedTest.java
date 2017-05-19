import junit.framework.TestCase;
import xyz.avarel.aje.Expression;
import xyz.avarel.aje.ast.Expr;

public class SpeedTest extends TestCase {
    public void testSpeeds() {
        // Performance hoggers
        // [1,2,[3,4,[5,6,[7,8],90, [91, 92],100]],9,10,[11, [50,52],12],13]
        // (8+2i)*(5i+3)
        // [1..10] * 10
        // ccccc
        String script = "1+2";
        int testsAmt = 10000;

        testPrecompiledSpeed(script, 100);
        testRecompileSpeed(script, 100);

        System.out.println(testsAmt + " Tests");
        System.out.println("Precompiled: " + testPrecompiledSpeed(script, testsAmt) + "ns avg");
        System.out.println("  Recompile: " + testRecompileSpeed(script, testsAmt) + "ns avg");
    }

    private long testPrecompiledSpeed(String script, long tests) {
        Expression exp = new Expression(script);

        Expr expr = exp.compile();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            expr.compute();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }

    private long testRecompileSpeed(String script, long tests) {
        Expression exp = new Expression(script);

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            Expr expr = exp.compile(true);
            expr.compute();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }
}
