import xyz.hexav.aje.Function;
import xyz.hexav.aje.FunctionBuilder;

public class OtherTest
{
    public static void main(String[] args)
    {
        Function func = new FunctionBuilder("add", "x + y")
                .addParameter("x", "y")
                .build()
                .input("x", 1)
                .input("y", 2);
    
        System.out.println(func.eval());
    }
}
