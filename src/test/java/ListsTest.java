import junit.framework.TestCase;
import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.MathExpression;

public class ListsTest extends TestCase {
    public void testAdd() {
//        AJEValue r1 = new AJEList(1, 2, 3).add(() -> 3);
//        System.out.println(r1.asString());
//        assertEquals(r1.asString(), "[4.0, 5.0, 6.0]");
    }

    public void testGet() {
        MathExpression exp = new ExpressionBuilder("[0..10]@index")
                .addVariable("index")
                .build()
                .compile();

        for (double i = 0; i < 10; i++) {
            assertEquals(exp.setVariable("index", i).value(), i);
        }
    }

    public void testGetRange() {
        MathExpression exp = new ExpressionBuilder("[0..10]@index")
                .addVariable("index")
                .build()
                .compile();

        System.out.println("after compile");

        for (int i = 0; i < 10; i++) {
            assertEquals(exp.setVariable("index", i).value(), (double) i);
        }
    }
}
