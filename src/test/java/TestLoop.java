import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.MathExpression;
import xyz.hexav.aje.types.interfaces.Value;

import java.util.Scanner;

public class TestLoop {
    public static void main(String[] args) {
        System.out.println("MATH EXPRESSION EVALUATOR");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        while (running) {
            try {
                System.out.print("Script | ");

                String input = sc.nextLine();

                switch (input) {
                    case "-stop":
                        running = false;
                        continue;
                }

                //Function exp = new Function(input);

                MathExpression exp = new ExpressionBuilder(input)
                        .addVariable("tau")
                        .build();
//                        .setVariable("tau", Math.PI * 2);

                //System.out.println(function);

                long start = System.nanoTime();
                Value result = exp.eval();
                long end = System.nanoTime();

                Object obj = result.toNativeObject();

                long ns =  (end - start);
                double ms = ns / 1000000D;

                System.out.println("  Time | " + ms + "ms " + ns + "ns" );
                System.out.println("Result | " + result + " : " + result.getType());
                System.out.println("Native | " + obj + " : " + obj.getClass().getSimpleName());

                System.out.println();
            } catch (RuntimeException e) {
                System.out.println("    Result | Caught an error: " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}