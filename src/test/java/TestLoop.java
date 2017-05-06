import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.MathExpression;

import java.util.Scanner;

public class TestLoop {
    public static void main(String[] args) {
        System.out.println("MATH EXPRESSION EVALUATOR");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        while (running) {
            try {
                System.out.print("Expression | ");

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
                exp.compile();
                long end = System.nanoTime();

                System.out.println("   Compile | " + (end - start));
                System.out.println("    Result | " + exp.eval());

                System.out.println();
            } catch (RuntimeException e) {
                System.out.println("    Result | Caught an error: " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}