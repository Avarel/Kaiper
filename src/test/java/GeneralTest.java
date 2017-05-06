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

        System.out.println(exp.eval());
    }


}
