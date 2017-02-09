package xyz.hexav.aje.expressions;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.FunctionBuilder;
import xyz.hexav.aje.defaults.DefaultOperators;
import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.operators.OperatorMap;
import xyz.hexav.aje.operators.Precedence;
import xyz.hexav.aje.pool.Pool;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiles string expressions into compiled Expression instances.
 */
public class ExpressionCompiler extends TokenizingUnit
{
    private final Pool pool;
    
    public ExpressionCompiler(Pool pool)
    {
        this.pool = pool;
    }
    
    /** Return the next String name. */
    private String nextLiteral()
    {
        int start = pos;
        
        if (!Character.isLetter(current))
        {
            throw makeError("Nominal literals must start with a letter.");
        }
        
        while (isLiteral(current)) nextChar();
        return line.substring(start, pos);
    }
    
    private static boolean isLiteral(char c)
    {
        return Character.isLetter(c) || c >= '0' && c <= '9' || c == '_';
    }
    
    private static boolean isNumeric(char c)
    {
        return c >= '0' && c <= '9' || c == '.';
    }
    
    public List<Expression> compileScript(String script)
    {
        return compileLines(toLines(script));
    }
    
    
    public List<Expression> compileScripts(List<String> scripts)
    {
        List<String> lines = new ArrayList<>();
        
        for (String script : scripts)
        {
            lines.addAll(toLines(script));
        }
    
        return compileLines(lines);
    }
    
    private List<String> toLines(String script)
    {
        char s_current = script.charAt(++pos);
    
        List<String> lines = new ArrayList<>();
    
        do
        {
            int start = pos;
        
            while (s_current != ';' && ++pos < script.length())
            {
                s_current = script.charAt(pos);
            }
        
            lines.add(script.substring(start, pos).trim());
        
            if (s_current != ';') break;
            else s_current = ++pos < script.length() ? script.charAt(pos) : (char) -1;
        }
        while (pos < script.length());
        
        return lines;
    }
    
    private List<Expression> compileLines(List<String> lines)
    {
        List<Expression> exps = new ArrayList<>();
        
        // Compile values and functions first.
        for (String line : lines)
        {
            resetPosition();
            setLine(line);
            nextChar();
            
            if (consume("func"))
            {
                consumeFunction();
            }
            else
            {
                exps.add(compileLine(line));
            }
        }
        
        return exps;
    }
    
    private Expression compileLine(String target)
    {
        setLine(target);
        resetPosition();
        nextChar();
        
        Expression exp = compileExpression();
        
        if (pos < line.length()) throw makeError("Unhandled character `"+current+"`.");
        
        return exp;
    }
    
    private Expression compileVariable()
    {
        if (isLiteral(current))
        {
            String name = nextLiteral();
        
            if (pool.hasVar(name))
            {
                throw makeError("Variable '" + name + "' is already defined.");
            }
            
            if (!consume('='))
            {
                return new VariableAssignment(pool.allocVar(name));
            }
            else
            {
                return new VariableAssignment(pool.allocVar(name), compileExpression());
            }
            
           
        }
        throw makeError("Expected name for variable.");
    }
    
    private Expression compileValue()
    {
        if (isLiteral(current))
        {
            String name = nextLiteral();
            
            if (pool.hasVar(name))
            {
                throw makeError("Value '" + name + "' is already defined.");
            }
            
            if (!consume('='))
            {
                return new VariableAssignment(pool.allocVal(name));
            }
            else
            {
                return new VariableAssignment(pool.allocVal(name), compileExpression());
            }
        }
        throw makeError("Expected name for value.");
    }
    
    private void consumeFunction()
    {
        if (isLiteral(current))
        {
            String name = nextLiteral();
            int params = 0;
            
            FunctionBuilder fb = new FunctionBuilder(name);
            
            if (!consume('('))
            {
                throw makeError("Expected '('.");
            }
    
            if (!consume(')')) do
            {
                fb.addParameter(nextLiteral());
                params++;
            }
            while (!consume(')') && consume(','));
            
            if (pool.hasFunc(name, params))
            {
                throw makeError("Function '" + name + "' is already defined.");
            }
            
            if (!consume('='))
            {
                throw makeError("Expected '='.");
            }
            skipWS();
            
            int start = pos;
            while (!nextIs(';') && pos < line.length()) nextChar();
            String script = this.line.substring(start, pos);
            
            fb.addLine(script);
            
            pool.allocFunc(fb.build());

            return;
        }
        throw makeError("Expected function name.");
    }
    
    private Expression compileExpression()
    {
        return compileExpression(pool.getOperators().firstPrecedence());
    }
    
    private Expression compileExpression(int level)
    {
        if (level == -1) return compileMisc();
        
        OperatorMap operators = pool.getOperators();
        
        // Unary operations
        for (Operator operator : operators.get(level))
        {
            if (operator.args == -1 && consume(operator.symbol))
            {
                Expression exp = compileExpression(level);
                return operator.compile(exp);
            }
        }
        
        Expression value = compileExpression(operators.after(level));
        
        while (true)
        {
            boolean flag = false;
            for (Operator operator : operators.get(level))
            {
                if (operator.args == 2 && consume(operator.symbol))
                {
                    final Expression
                            a = value,
                            b = compileExpression(
                                operator.next != -1 ?
                                operator.next :
                                operators.after(level));
                    
                    value = operator.compile(a, b);
                    
                    flag = true;
                }
                else if (operator.args == 1 && consume(operator.symbol))
                {
                    value = operator.compile(value);
                    flag = true;
                }
            }
            if (!flag) break;
        }
        
        return value;
    }
    
    private Expression compileMisc()
    {
        Expression value;
        if (consume('('))
        {
            value = compileExpression();
            consume(')');
        }
        else value = compileLiteral();
        
        // Implicit multiplications.
        if (nextIs('(') || isLiteral(current))
        {
            int _pos = pos;
            char _current = current;
            try
            {
                final Expression
                        a = value,
                        b = compileMisc();
                
                value = DefaultOperators.MULTIPLY.get().compile(a, b);
            }
            catch (RuntimeException e)
            {
                pos = _pos;
                current = _current;
            }
        }
        
        return value;
    }
    
    private Expression compileLiteral()
    {
        int start = pos;
        
        // DEFINE VALUES
        // - NUMERICAL PARSING
        
        if (consume("var"))
        {
            return compileVariable();
        }
        else if (consume("val"))
        {
            return compileValue();
        }
        
        if (isNumeric(current))
        {
            if (consume('0'))
            {
                // BINARY
                if (consume('b'))
                {
                    int _start = pos;
                    
                    while (isNumeric(current))
                    {
                        if (!(current == '0' || current == '1'))
                        {
                            throw makeError("Binary literals can only have '1' and '0'.");
                        }
                        nextChar();
                    }
                    return Expression.ofValue(Integer.valueOf(line.substring(_start, pos), 2).doubleValue());
                }
                // HEXADECIMAL
                else if (consume('x'))
                {
                    int _start = pos;
                    
                    while (isLiteral(current))
                    {
                        if (!(current >= '0' && current <= '9' || current >= 'A' && current <= 'F'))
                        {
                            throw makeError("Hexadecimal literals can only have '1-9' and 'A-F'.");
                        }
                        nextChar();
                    }
                    return Expression.ofValue(Integer.valueOf(line.substring(_start, pos), 16).doubleValue());
                }
            }
            while (isNumeric(current)) nextChar();
            
            double value = Double.parseDouble(line.substring(start, pos));
            return Expression.ofValue(value);
        }
        // INTERVALS
        else if (consume('['))
        {
            if (consume(']')) return Expression.NOTHING;

            List<Expression> items = new ArrayList<>();
            
            do
            {
                Expression a = compileExpression();

                if (consume("..."))
                {
                    Expression b = compileExpression();
                    
                    Expression c = items.size() == 1 ? items.get(items.size() - 1) : null;
                    
                    items.add(Expression.ofRange(a, b, c));
                }
                else
                {
                    items.add(a);
                }
            }
            while (consume(','));
            
            if (!consume(']')) throw makeError("Expected list literal to close.");
            
            return Expression.ofList(items);
        }

        // FUNCTION AND CONSTANTS
        else if (Character.isLetter(current))
        {
            String name = nextLiteral();

            if (pool.hasVar(name))
            {
                return new VariableExpression(pool.getVar(name));
            }

            // FUNCTIONS LOOKUP
            List<Expression> args = new ArrayList<>();

            if (consume('('))
            {
                if (!consume(')')) do
                {
                    Expression exp;

                    if (consume('*'))
                    {
                        exp = compileExpression().spread();
                    }
                    else exp = compileExpression();
                    args.add(exp);
                }
                while (!consume(')') && consume(','));
            }
            else
            {
                do
                {
                    Expression exp;

                    if (consume('*'))
                    {
                        exp = compileExpression(Precedence.MULTIPLICATIVE).spread();
                    }
                    else exp = compileExpression(Precedence.MULTIPLICATIVE);
                    args.add(exp);
                }
                while (consume(','));
            }

            if (pool.hasFunc(name, args.size()))
            {
                return new FunctionExpression(pool.getFunc(name, args.size()), args);
            }
            else throw makeError("Can not resolve reference `" + name + "`.");
        }
        else throw makeError("Unexpected token: " + current);
    }
    
    private AJEException makeError(String message)
    {
        return new AJEException(message + " " + currentInfo());
    }
}
