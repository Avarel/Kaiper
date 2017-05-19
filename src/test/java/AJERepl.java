import xyz.avarel.aje.Expression;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Any;

import java.util.Scanner;

public class AJERepl {
    public static void main(String[] args) {
        System.out.println("AJE REPL");
        System.out.println();

        Scanner sc = new Scanner(System.in).useDelimiter("(?<!-)[\\r\\n]+");

        boolean running = true;

        while (running) {
            try {
                System.out.print("  REPL | ");

                String input = sc.nextLine();

                switch (input) {
                    case "-stop":
                        running = false;
                        continue;
                }

                //Function exp = new Function(input);


                //System.out.println(" Lexer | " + new AJELexer(input));

                Expression exp = new Expression(input);

                long start = System.nanoTime();
                Expr expr = exp.compile();
                Any result = expr.compute();
                long end = System.nanoTime();

                Object obj = result.toNative();

                long ns =  (end - start);
                double ms = ns / 1000000D;

                System.out.println("Result | " + result + " : " + result.getType());
                System.out.println("  Time | " + ms + "ms " + ns + "ns" );

                StringBuilder builder = new StringBuilder();

                expr.ast(builder, "\t\t ", true);

                System.out.println("   AST > ⚫\n" + builder);

                System.out.println();
            } catch (RuntimeException e) {
                System.out.println(" ERROR | " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}