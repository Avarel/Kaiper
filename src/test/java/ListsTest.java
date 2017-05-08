//import junit.framework.TestCase;
//import xyz.avarel.aje.ExpressionBuilder;
//import xyz.avarel.aje.MathExpression;
//
//public class ListsTest extends TestCase {
//    public void testAdd() {
////        AJEValue r1 = new AJEList(1, 2, 3).add(() -> 3);
////        System.out.println(r1.asString());
////        assertEquals(r1.asString(), "[4.0, 5.0, 6.0]");
//    }
//
//    public void testGet() {
//        MathExpression exp = new ExpressionBuilder("[0..10]@index")
//                .addVariable("index")
//                .build()
//                .compile();
//
////        for (double i = 0; i < 10; i++) {
////            assertEquals(exp.setVariable("index", i).eval(), i);
////        }
//    }
//
//    public void testVirtualFlattening() {
//        MathExpression exp = new ExpressionBuilder("[1,2,[3,4,[5,6,[7,8],90, [91, 92],100]],9,10,[11, [50,52],12],13]")
//                .build().compile();
//
////        System.out.println(exp.asString());
//    }
//
//    public void testGetRange() {
//        MathExpression exp = new ExpressionBuilder("[0..10]@index")
//                .addVariable("index")
//                .build()
//                .compile();
//
//        System.out.println("after compile");
//
////        for (int i = 0; i < 10; i++) {
////            assertEquals(exp.setVariable("index", i).eval(), (double) i);
////        }
//    }
//}
