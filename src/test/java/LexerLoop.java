import xyz.avarel.aje.parserRewrite.Lexer;

import java.util.Scanner;

public class LexerLoop {
    public static void main(String[] args) {
        System.out.println("LEXER EVALUATOR");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        while (running) {
            try {
                System.out.print("String | ");

                String input = sc.nextLine();

                switch (input) {
                    case "-stop":
                        running = false;
                        continue;
                }

                Lexer lexer = new Lexer(input);

                System.out.println(lexer);

            } catch (RuntimeException e) {
                System.out.println("    Result | Caught an error: " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}