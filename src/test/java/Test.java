import xyz.hexavalon.aje.Function;

import java.util.Arrays;
import java.util.Scanner;

public class Test
{
    public static void main(String[] args)
    {
        System.out.println("MATH EXPRESSION EVALUATOR");
        System.out.println();
        
        Scanner sc = new Scanner(System.in);
        
        boolean running = true;
        
        while (running)
        {
            try
            {
                System.out.print("Expression | ");
                
                String input = sc.nextLine();
                
                switch(input)
                {
                    case "-stop":
                        running = false;
                        continue;
                }
                
                //Function exp = new Function(input);
    
                Function function = new Function(input);
    
                //System.out.println(function);
                
                long start = System.nanoTime();
                
                double[] values = function.evalList();
                //List<Number> results = exp.evalScript();
                
                long end = System.nanoTime();
                
                //System.out.println("    Result | " + results + "");
                System.out.println("    Result | " + Arrays.toString(values));
                System.out.println("   Elasped | " + (end - start));
                System.out.println();
            }
            catch (RuntimeException e)
            {
                System.out.println("    Result | Caught an error: "+e.getMessage()+"\n");
                e.printStackTrace();
                return;
            }
        }
    }
}