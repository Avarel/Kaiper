import xyz.hexav.aje.ExpressionBuilder;
import xyz.hexav.aje.types.AJEValue;

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

                AJEValue exp = new ExpressionBuilder(input)
                        .addVariable("tau")
                        .build()
                        .setVariable("tau", Math.PI * 2);

                //System.out.println(function);

                long start = System.nanoTime();

                System.out.println("    Result | " + exp.asString());
                //List<Number> results = exp.evalScript();

                long end = System.nanoTime();

                //System.out.println("    Result | " + results + "");

                System.out.println("   Elasped | " + (end - start));
                System.out.println();
            } catch (RuntimeException e) {
                System.out.println("    Result | Caught an error: " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}