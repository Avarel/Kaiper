
AJE
===
AJE is a math-centered scripting language/expression evaluator for the Java programming language.

### Features
* Mathematical evaluation.
* Built-in functions and operators.
* Lists literals and operations.
* User-defined variables and functions.

#### Get Started
Get started by importing the `ExpressionBuilder.java` class.
Create a new instance with the desired script, then add additional lines of script, functions, operators and variables.
```java
import xyz.hexav.aje.ExpressionBuilder;

Expression exp = new ExpressionBuilder("[1,2,3] * tau")
                        .addVariable("tau")
                        .build()
                        .setVariable("tau", Math.PI * 2);
                        
double[] result = exp.evalList();
// Expression#evalList() allows for the evaluation of AJE's list feature,
```
A similar builder class is also available for creating Functions, it is the `FunctionBuilder.java` class.
```java
import xyz.hexav.aje.FunctionBuilder;

Function func = new FunctionBuilder("add", "x + y")
                .addParameter("x", "y")
                .build()
                .input("x", 1)
                .input("y", 2);
                
double result = func.eval();
```

#### Variable
Define variables and use them. Reassignment is also possible.
```
Expression | var tau = 2pi; sin .25tau
    Result | [1.0]
    
Expression | var lists = [1,2,3]; 2*lists
    Result | [2.0, 4.0, 6.0]
    
Expression | var x = 2; x = x + 1;
    Result | [3.0]
```

#### Functions
Define your own functions and evaluate them. You can explicitly use parentheses but it is optional.
```
Expression | func f(x) = 2x; [f(2), f(3), f(4)]
    Result | [4.0, 6.0, 8.0]
    
Expression | max 2,3
    Result | [3.0]
    
Expression | min(6,7)
    Result | [6.0]
```
You can use the list notation to evaluate the function multiple times with different arguments.
```
Expression | func f(x) = 2x; f([2 ... 4])
    Result | [4.0, 6.0, 8.0]
```
If you want to spread the arguments for certain var-args functions, you can prefix it with a `*`.
```
Expression | var x = [1 ... 6]; sum *x
    Result | [21.0]
    
Expression | sum *[1 ... 6]
    Result | [21.0]
```

#### Lists
Quickly find the value of a series of values inputted into an operation or function. Like killing 2 birds with one stone.
```
Expression | [1,2,3] * 2
    Result | [2.0, 4.0, 6.0]
   
Expression | [1,2,3,4,5]@[1 ... 3]
    Result | [2.0, 3.0, 4.0]

Expression | [1, -1 ... -9]
    Result | [1.0, -1.0, -3.0, -5.0, -7.0, -9.0]
```
Lists can be concatenated by inserting them inside a new list literal, like `[list_a, list_b]`.
To access a specific element of a list, use the access operator: `[list]@index` like `[1,2,3,4,5]@2`. You can also perform a sub-list operation using the same operator, by doing `[list]@[range]` like `[1,2,3,4,5]@[1 ... 3]`.
To define a simple interval/range, just use the range notation: `[start ... end]`. If you want to obtain spaced intervals within lists, the list must have exactly only 3 explicit contents: `[first, second ... last]`. The change in each value is defined as the second minus the first item.