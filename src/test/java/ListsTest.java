import junit.framework.TestCase;
import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.MathExpression;

public class ListsTest extends TestCase {
    public void testGet() {
        MathExpression exp = new ExpressionBuilder("[0 ... 10]@index")
                .addVariable("index")
                .build()
                .compile();

        for (double i = 0; i < 10; i++) {
            assertEquals(exp.setVariable("index", i).eval(), i);
        }
    }

    public void testGetRange() {
        MathExpression exp = new ExpressionBuilder("[0 ... 10]@index")
                .addVariable("index")
                .build()
                .compile();

        for (double i = 0; i < 10; i++) {
            assertEquals(exp.setVariable("index", i).eval(), i);
        }
    }
}
