import xyz.avarel.aje.parserRewrite.AJEParser2;
import xyz.avarel.aje.parserRewrite.Lexer;

import java.util.Scanner;

public class NewParserLoop {
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

                AJEParser2 parser = new AJEParser2(lexer);

                System.out.println(parser.parse());

            } catch (RuntimeException e) {
                System.out.println("    Result | Caught an error: " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}