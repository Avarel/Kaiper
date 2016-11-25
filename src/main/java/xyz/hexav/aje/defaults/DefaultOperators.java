package xyz.hexav.aje.defaults;

import xyz.hexav.aje.expressions.Expression;
import xyz.hexav.aje.expressions.VariableExpression;
import xyz.hexav.aje.operators.Precedence;
import xyz.hexav.aje.expressions.VariableAssignment;
import xyz.hexav.aje.operators.Operator;

public enum DefaultOperators
{
    LOGICAL_OR(new Operator("||", 2, args -> args[0] != 0 || args[1] != 0 ? 1 : 0)),
    LOGICAL_AND(new Operator("&&", 2, args -> args[0] != 0 && args[1] != 0 ? 1 : 0)),
    
    ADD(new Operator("+", 2, args -> args[0] + args[1])),
    SUBTRACT(new Operator("-", 2, args -> args[0] - args[1])),
    
    MULTIPLY(new Operator("*", 2, args -> args[0] * args[1])),
    DIVIDE(new Operator("/", 2, args -> args[0] / args[1])),
    REMAINDER(new Operator("%", 2, args -> args[0] % args[1])),
    MODULUS(new Operator("mod", 2, args -> (((args[0] % args[1]) + args[1]) % args[1]))),
    PERCENTAGE(new Operator("% of ", 2, args -> args[0] / 100 * args[1])),
    N_ROOT(new Operator("th root of ", 2, args -> Math.pow(args[1], 1.0 / args[0]))),
    
    EQUALS(new Operator("==", 2, args -> args[0] == args[1] ? 1 : 0)),
    NOT_EQUALS(new Operator("!=", 2, args -> args[0] != args[1] ? 1 : 0)),
    
    GREATER_OR_EQUAL(new Operator(">=", 2, args -> args[0] >= args[1] ? 1 : 0)),
    GREATER_THAN(new Operator(">", 2, args -> args[0] > args[1] ? 1 : 0)),
    LESSER_OR_EQUAL(new Operator("<=", 2, args -> args[0] <= args[1] ? 1 : 0)),
    LESSER_THAN(new Operator("<", 2, args -> args[0] < args[1] ? 1 : 0)),
    
    ZERO_FILL_RIGHT_SHIFT(new Operator(">>>", 2, args -> (int) args[0] >>> (int) args[1])),
    RIGHT_SHIFT(new Operator(">>", 2, args -> (int) args[0] >> (int) args[1])),
    LEFT_SHIFT(new Operator("<<", 2, args -> (int) args[0] << (int) args[1])),
    
    UNARY_PLUS(new Operator("+", -1, args -> +args[0])),
    UNARY_MINUS(new Operator("-", -1, args -> -args[0])),
    BITWISE_COMPLEMENT(new Operator("~", -1, args -> ~(int) args[0])),
    LOGICAL_NOT(new Operator("!", -1, args -> args[0] == 0 ? 1 : 0)),
    
    EXPONENTATION(new Operator("^", 2, Precedence.UNARY, args -> Math.pow(args[0], args[1]))),
    SCIENTIFIC_EX(new Operator("E", 2, Precedence.UNARY, args -> args[0] * Math.pow(10, args[1]))),
    
    DEGREES(new Operator("deg", 1, args -> Math.toRadians(args[0]))),
    ITEM_AT_LIST(new Operator("@", 2) {
        @Override
        public Expression compile(Expression a, Expression b)
        {
            return () ->
            {
                double[] items = a.evalList();
                double[] access = b.evalList();
                
                double[] results = new double[access.length];
                
                for (int i = 0; i < access.length; i++)
                {
                    results[i] = items[(int) access[i]];
                }
                
                return results;
            };
        }
    }),
    
    POST_INCREMENT(new Operator("++", 1) {
        @Override
        public Expression compile(Expression a)
        {
            if (!(a instanceof VariableExpression))
            {
                throw new RuntimeException("Left of assignment operation must be a variable expression.");
            }
        
            VariableExpression _a = (VariableExpression) a;
        
            return new VariableAssignment(_a.getVariable(), () -> new double[] { _a.eval() + 1 })
            {
                @Override
                public double[] evalList()
                {
                    double[] result = getVariable().eval();
                    if (getExpression() != null) getVariable().assign(getExpression().evalList());
                    return result;
                }
            };
        }
    }),
    POST_DECREMENT(new Operator("--", 1) {
        @Override
        public Expression compile(Expression a)
        {
            if (!(a instanceof VariableExpression))
            {
                throw new RuntimeException("Left of assignment operation must be a variable.");
            }
            
            VariableExpression _a = (VariableExpression) a;
            
            return new VariableAssignment(_a.getVariable(), () -> new double[] { _a.eval() - 1 })
            {
                @Override
                public double[] evalList()
                {
                    double[] result = getVariable().eval();
                    if (getExpression() != null) getVariable().assign(getExpression().evalList());
                    return result;
                }
            };
        }
    }),
    
    PRE_INCREMENT(new Operator("++", -1) {
        @Override
        public Expression compile(Expression a)
        {
            if (!(a instanceof VariableExpression))
            {
                throw new RuntimeException("Left of assignment operation must be a variable expression.");
            }
            
            VariableExpression _a = (VariableExpression) a;
            
            return new VariableAssignment(_a.getVariable(), () -> new double[] { _a.eval() + 1 });
        }
    }),
    PRE_DECREMENT(new Operator("--", -1) {
        @Override
        public Expression compile(Expression a)
        {
            if (!(a instanceof VariableExpression))
            {
                throw new RuntimeException("Left of assignment operation must be a variable.");
            }
            
            VariableExpression _a = (VariableExpression) a;
            
            return new VariableAssignment(_a.getVariable(), () -> new double[] { _a.eval() - 1 });
        }
    }),
    
    VAR_ASSIGNMENT(new Operator("=", 2) {
        @Override
        public Expression compile(Expression a, Expression b)
        {
            if (!(a instanceof VariableExpression))
            {
                throw new RuntimeException("Left of assignment operation must be a variable.");
            }
            
            VariableExpression _a = (VariableExpression) a;
            
            return new VariableAssignment(_a.getVariable(), b);
        }
    }),
    
    ;
    
    private final Operator operator;
    
    DefaultOperators(Operator operator)
    {
        this.operator = operator;
    }
    
    public Operator get()
    {
        return operator;
    }
}
